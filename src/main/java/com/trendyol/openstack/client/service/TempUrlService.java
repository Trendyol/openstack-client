package com.trendyol.openstack.client.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.trendyol.openstack.client.config.DefaultOpenStackProperties;
import com.trendyol.openstack.client.config.OpenstackAuthService;
import com.trendyol.openstack.client.model.exception.SwiftClientException;
import com.trendyol.openstack.client.model.prop.openstack.tempurl.TempURLMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
class TempUrlService {
    private static final String CACHE_TEMP_KEY = "set-temp-key";
    private final OpenstackAuthService openstackAuthService;
    private final DefaultOpenStackProperties properties;
    private final RestTemplate openstackRestTemplate;
    private LoadingCache<String, Boolean> setTempKeyCache;

    @PostConstruct
    public void initialize() {
        setTempKeyCache = CacheBuilder.newBuilder()
                .refreshAfterWrite(Duration.ofMinutes(properties.getMeta().getSetTempKeyCycleMinutes()))
                .build(new CacheLoader<>() {
                    @Override
                    public Boolean load(String cacheKey) {
                        try {
                            return setTempKey();
                        } catch (Exception exception) {
                            log.error("SetTempKey refreshable cache has an error", exception);
                            return false;
                        }
                    }
                });
        setTempKeyCache.getUnchecked(CACHE_TEMP_KEY);
    }

    public String getTempUrl(String region, TempURLMethod method, String containerName, String objectPath, long expiry) {
        return switch (method) {
            case PUT -> create(region, properties.getObjectStorageHost(), method, containerName, objectPath, expiry);
            case GET -> create(region, properties.getStoragePublicHost(), method, containerName, objectPath, expiry);
            default ->
                    throw SwiftClientException.builder().message(String.format("Invalid HTTP method: '%s'", method.toString())).build();
        };
    }

    private String getTempURLSecretKey(String region) {
        setTempKeyCache.getUnchecked(CACHE_TEMP_KEY);
        return properties.getTempUrlKeys().stream()
                .filter(a -> region.equals(a.getRegion()))
                .map(keyConf -> {
                    if (StringUtils.hasText(keyConf.getKey2())) {
                        if (ZonedDateTime.now(ZoneOffset.UTC).getSecond() % 2 == 0) {
                            return keyConf.getKey1();
                        }
                        return keyConf.getKey2();
                    }
                    return keyConf.getKey1();
                }).findFirst()
                .orElseThrow(() -> SwiftClientException.builder().message("Temp url key not found in region=" + region).build());
    }

    private String create(String region, String hostname, TempURLMethod method, String containerName, String objectPath, long expiry) {
        try {
            String path = properties.getMeta().getSwiftBasePath() + containerName + properties.getMeta().getDelimeter() + objectPath;
            long expiration = Instant.now().getEpochSecond() + expiry;
            String valueToDigest = String.format("%s\n%d\n%s", method.toString(), expiration, path);
            HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, getTempURLSecretKey(region));
            String hmac = hmacUtils.hmacHex(valueToDigest);
            return String.format("%s%s?temp_url_sig=%s&temp_url_expires=%d",
                    hostname, path, hmac, expiration);
        } catch (Exception e) {
            throw SwiftClientException.builder()
                    .message("An exception has occurred while executing getTempUrl()")
                    .cause(e)
                    .build();
        }
    }

    public boolean setTempKey() {
        try {
            properties.getTempUrlKeys()
                    .forEach(tp -> {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add(properties.getMeta().getAuthTokenKey(), openstackAuthService.getSession().useRegion(tp.getRegion()).getToken().getId());
                        headers.add(properties.getMeta().getTempUrlKeyHeader(), tp.getKey1());
                        headers.add(properties.getMeta().getTempUrlKeyHeader2(), tp.getKey2());
                        log.info("SetTempKey Headers: {}", headers);
                        HttpEntity<String> entity = new HttpEntity<>("body", headers);
                        openstackRestTemplate.exchange(properties.getObjectStorageHost() + properties.getMeta().getSwiftBasePath(), HttpMethod.POST, entity, Void.class);
                        log.info("Setting up temp url key for account in openstack succeeded.. Region: {}", tp.getRegion());
                    });
            return true;
        } catch (Exception e) {
            log.error("An exception has occurred while executing setTempKey(), it will be retry now, exception: {}", e.getMessage(), e);
        }
        return false;
    }
}
