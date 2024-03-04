package com.trendyol.openstack.client.extension;

import com.trendyol.openstack.client.config.DefaultOpenStackProperties;
import com.trendyol.openstack.client.model.exception.SwiftClientException;
import org.openstack4j.openstack.identity.v3.domain.KeystoneToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;


public interface ValidatorExtension extends FunctionalExtension, DateExtension {
    Logger log = LoggerFactory.getLogger(ValidatorExtension.class);

    default boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    default void checkDefaults(DefaultOpenStackProperties openStackProperties) {
        Optional.ofNullable(openStackProperties.getDefaultRegion())
                .ifPresentOrElse(defaultRegion -> log.debug("Default Region is defined."),
                        throwExceptionRunnable("OpenstackClient: ${openstack.swift.default-region} is missing in configuration"));

        Optional.ofNullable(openStackProperties.getProjectId())
                .ifPresentOrElse(defaultRegion -> log.debug("Project Id is defined."),
                        throwExceptionRunnable("OpenstackClient: ${openstack.swift.project-id} is missing in configuration"));

        Optional.ofNullable(openStackProperties.getAuthHost())
                .filter(this::isValidURL)
                .ifPresentOrElse(authHost -> log.debug("Auth host is OK"),
                        throwExceptionRunnable("OpenstackClient: ${openstack.swith.auth-host} not valid"));

        Optional.ofNullable(openStackProperties.getObjectStorageHost())
                .filter(this::isValidURL)
                .ifPresentOrElse(host -> log.debug("Object Storage Host is OK"),
                        throwExceptionRunnable("OpenstackClient: ${openstack.swith.object-storage-host} not valid"));

        Optional.ofNullable(openStackProperties.getStoragePublicHost())
                .filter(this::isValidURL)
                .ifPresentOrElse(host -> log.debug("Storage Public Host is OK"),
                        throwExceptionRunnable("OpenstackClient: ${openstack.swith.storage-public-host} not valid"));
    }

    default void checkCredentials(DefaultOpenStackProperties openStackProperties) {

        if (Optional.ofNullable(openStackProperties.getApplicationCredential()).isPresent()) {
            Optional.of(openStackProperties.getApplicationCredential())
                    .filter(ac -> Optional.ofNullable(ac.getCredentialId()).isPresent())
                    .filter(ac -> Optional.ofNullable(ac.getSecret()).isPresent())
                    .ifPresentOrElse(defaultRegion -> log.debug("Application Credentials is defined."),
                            throwExceptionRunnable("OpenstackClient: ${openstack.swift.application-credential} is missing due to choosed credential method.(Application)"));
        } else if (Optional.ofNullable(openStackProperties.getUsernamePasswordCredential()).isPresent()) {
            Optional.of(openStackProperties.getUsernamePasswordCredential())
                    .filter(ac -> Optional.ofNullable(ac.getId()).isPresent())
                    .filter(ac -> Optional.ofNullable(ac.getPassword()).isPresent())
                    .ifPresentOrElse(defaultRegion -> log.debug("Username/Password Credentials is defined."),
                            throwExceptionRunnable("OpenstackClient: ${openstack.swift.username-password-credential} is missing due to choosed credential method.(Username/Password)"));
        } else {
            throw SwiftClientException.builder().message("OpenstackClient: Credentials are not defined in configuration. Please choose one of ${openstack.swift.application-credential} / ${openstack.swift.username-password-credential}").build();
        }
    }

    default void checkTempUrlKeys(DefaultOpenStackProperties openStackProperties) {
        if (CollectionUtils.isEmpty(openStackProperties.getTempUrlKeys())) {
            throw SwiftClientException.builder().message("OpenstackClient: Temp url set keys are not defined in configuration").build();
        }
        if (!openStackProperties
                .getTempUrlKeys()
                .stream()
                .allMatch(tk -> StringUtils.hasText(tk.getRegion())
                                && StringUtils.hasText(tk.getKey1())
                                && StringUtils.hasText(tk.getKey2()))) {
            throw SwiftClientException.builder().message("OpenstackClient: Temp Url Set Keys have empty fields in confuguration.").build();
        }

    }

    default void checkAndRefreshToken(KeystoneToken keystoneToken, SupplyVoid func) {
        if (Optional.ofNullable(keystoneToken).isEmpty() || LocalDateTime.now().plusHours(1).isAfter(convertToLocalDateTimeViaInstant(keystoneToken.getExpires()))) {
            func.process();
        }
    }
}
