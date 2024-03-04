package com.trendyol.openstack.client.config;

import com.trendyol.openstack.client.model.prop.openstack.ApiMetaProp;
import com.trendyol.openstack.client.model.prop.openstack.ApplicationCredentialProp;
import com.trendyol.openstack.client.model.prop.openstack.UsernamePasswordCredentialProp;
import com.trendyol.openstack.client.model.prop.openstack.WebClientProp;
import com.trendyol.openstack.client.model.prop.openstack.tempurl.TempUrlKeyProp;
import com.trendyol.openstack.client.util.SwiftRestClientYamFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "openstack.swift")
@Data
@NoArgsConstructor
@AllArgsConstructor
@PropertySource(value = "classpath:swift-rest-client-defaults.yml", factory = SwiftRestClientYamFactory.class)
public class DefaultOpenStackProperties {
    private ApiMetaProp meta;
    private String profile;
    private String authHost;
    private String objectStorageHost;
    private String storagePublicHost;
    private String projectId;
    private String defaultRegion;
    private ApplicationCredentialProp applicationCredential;
    private UsernamePasswordCredentialProp usernamePasswordCredential;
    private List<TempUrlKeyProp> tempUrlKeys;
    private WebClientProp webClient;
}
