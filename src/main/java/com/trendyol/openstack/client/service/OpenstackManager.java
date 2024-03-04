package com.trendyol.openstack.client.service;

import com.trendyol.openstack.client.config.DefaultOpenStackProperties;
import com.trendyol.openstack.client.config.OpenstackAuthService;
import com.trendyol.openstack.client.extension.FunctionalExtension;
import com.trendyol.openstack.client.model.prop.openstack.tempurl.TempURLMethod;
import com.trendyol.openstack.client.model.prop.openstack.tempurl.TempUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openstack4j.model.common.payloads.InputStreamPayload;
import org.openstack4j.model.storage.object.SwiftObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenstackManager implements FunctionalExtension {
    private final OpenstackAuthService openstackAuthService;
    private final TempUrlService tempUrlService;
    private final DefaultOpenStackProperties openStackProperties;

    public SwiftObject getObject(String containerName, String filePath) {
        return process(() -> getObject(openStackProperties.getDefaultRegion(), containerName, filePath));
    }

    public SwiftObject getObject(String region, String containerName, String filePath) {
        return process(() -> openstackAuthService.getSession().useRegion(region).objectStorage().objects().get(containerName, filePath));
    }

    public String putObject(String containerName, String filePath, byte[] fileBytes) {
        return process(() -> putObject(openStackProperties.getDefaultRegion(), containerName, filePath, fileBytes));
    }

    public String putObject(String region, String containerName, String filePath, byte[] fileBytes) {
        return process(() -> {
            InputStreamPayload payload = new InputStreamPayload(new ByteArrayInputStream(fileBytes));
            return openstackAuthService.getSession().useRegion(region).objectStorage().objects().put(containerName, filePath, payload);
        });
    }

    public String putObject(String containerName, String filePath, ByteArrayInputStream byteArrayInputStream) {
        return process(() -> putObject(openStackProperties.getDefaultRegion(), containerName, filePath, byteArrayInputStream));
    }

    public String putObject(String region, String containerName, String filePath, ByteArrayInputStream byteArrayInputStream) {
        return process(() -> {
            InputStreamPayload payload = new InputStreamPayload(byteArrayInputStream);
            return openstackAuthService.getSession().useRegion(region).objectStorage().objects().put(containerName, filePath, payload);
        });
    }

    public boolean existsObject(String containerNmae, String filePath) {
        return process(() -> existsObject(openStackProperties.getDefaultRegion(), containerNmae, filePath));
    }

    public boolean existsObject(String region, String containerNmae, String filePath) {
        return process(() -> Optional.ofNullable(openstackAuthService.getSession().useRegion(region).objectStorage().objects().get(containerNmae, filePath)).isPresent());
    }

    public boolean createContainer(String region, String containerName) {
        return process(() -> openstackAuthService.getSession().useRegion(region).objectStorage().containers().create(containerName).isSuccess());
    }

    public boolean createContainer(String containerName) {
        return process(() -> openstackAuthService.getSession().useRegion(openStackProperties.getDefaultRegion()).objectStorage().containers().create(containerName).isSuccess());
    }

    public TempUrl getDownloadTempUrl(String containerName, String filePath, long ttl) {
        return process(() -> getDownloadTempUrl(openStackProperties.getDefaultRegion(), containerName, filePath, ttl));
    }

    public TempUrl getDownloadTempUrl(String region, String containerName, String filePath, long ttl) {
        return process(() -> {
            String tempUrl = tempUrlService.getTempUrl(region, TempURLMethod.GET, containerName, filePath, ttl);
            return TempUrl.builder().tempUrl(tempUrl).build();
        });
    }

    public TempUrl getUploadTempUrl(String containerName, String filePath, long ttl) {
        return process(() -> getUploadTempUrl(openStackProperties.getDefaultRegion(), containerName, filePath, ttl));
    }

    public TempUrl getUploadTempUrl(String region, String containerName, String filePath, long ttl) {
        return process(() -> {
            String tempUrl = tempUrlService.getTempUrl(region, TempURLMethod.PUT, containerName, filePath, ttl);
            return TempUrl.builder().tempUrl(tempUrl).build();
        });
    }

    public boolean deleteObject(String containerName, String filePath) {
        return process(() -> deleteObject(openStackProperties.getDefaultRegion(), containerName, filePath));
    }

    public boolean deleteObject(String region, String containerName, String filePath) {
        return process(() -> openstackAuthService.getSession().useRegion(region).objectStorage().objects().delete(containerName, filePath).isSuccess());
    }

}
