package com.trendyol.openstack.client.config;

import com.trendyol.openstack.client.extension.ValidatorExtension;
import com.trendyol.openstack.client.model.client.auth.Project;
import com.trendyol.openstack.client.model.client.auth.Scope;
import com.trendyol.openstack.client.model.client.auth.appcredential.AppCredential;
import com.trendyol.openstack.client.model.client.auth.appcredential.AuthAppCredential;
import com.trendyol.openstack.client.model.client.auth.appcredential.Identity;
import com.trendyol.openstack.client.model.client.auth.password.AuthPassword;
import com.trendyol.openstack.client.model.client.auth.password.IdentityPassword;
import com.trendyol.openstack.client.model.client.auth.password.UsernamePasswordCredential;
import com.trendyol.openstack.client.model.prop.openstack.ApplicationCredentialProp;
import com.trendyol.openstack.client.model.prop.openstack.UsernamePasswordCredentialProp;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openstack4j.openstack.identity.v3.domain.KeystoneToken;
import org.openstack4j.openstack.identity.v3.domain.TokenAuth;
import org.openstack4j.openstack.internal.OSClientSession;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenstackAuthService implements ValidatorExtension {
    private final AtomicReference<KeystoneToken> keystoneTokenAtomic = new AtomicReference<>(null);
    private final DefaultOpenStackProperties openStackProperties;
    private final RestTemplate openstackRestTemplate;


    @PostConstruct
    public void initialize() {
        checkDefaults(openStackProperties);
        checkCredentials(openStackProperties);
        checkTempUrlKeys(openStackProperties);
        checkAndRefreshToken(null, this::refreshToken);
    }

    public OSClientSession.OSClientSessionV3 getSession() {
        checkAndRefreshToken(getKeyStone(), this::refreshToken);
        return OSClientSession.OSClientSessionV3.createSession(getKeyStone());
    }

    @SneakyThrows
    private void refreshToken() {
        Optional.ofNullable(openStackProperties.getApplicationCredential())
                .map(this::authenticateApplicationCredential)
                .or(() -> Optional.ofNullable(openStackProperties.getUsernamePasswordCredential())
                        .map(this::authenticateUsernamePassword))
                .orElseThrow(throwExceptionSupplier("Openstack authenticate has an error due to missing configuration"));
    }

    public boolean authenticateUsernamePassword(UsernamePasswordCredentialProp usernamePasswordCredential) {
        try {
            log.info("Started to refresh auth token with username/pass authentication");

            AuthPassword auth = AuthPassword.builder()
                    .identity(IdentityPassword.builder()
                            .methods(new String[]{"password"})
                            .user(
                                    UsernamePasswordCredential.builder()
                                            .user(new UsernamePasswordCredential.User(openStackProperties.getUsernamePasswordCredential().getId(),
                                                    openStackProperties.getUsernamePasswordCredential().getPassword()))
                                            .build()
                            ).build())
                    .scope(Scope.builder().project(Project.builder().id(openStackProperties.getProjectId()).build()).build())
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AuthPassword> httpRequest = new HttpEntity<>(auth, headers);
            ResponseEntity<KeystoneToken> keystoneTokenResponse =
                    openstackRestTemplate.postForEntity(openStackProperties.getAuthHost(), httpRequest, KeystoneToken.class);

            keystoneTokenResponse.getBody().setId(keystoneTokenResponse.getHeaders().getFirst("x-subject-token"));
            KeystoneToken incomingKeyStone = keystoneTokenResponse.getBody();
            log.info("Finished refreshing auth token with username/pass authentication: {}", incomingKeyStone);
            incomingKeyStone.applyContext(openStackProperties.getObjectStorageHost().concat(openStackProperties.getMeta().getSwiftBasePath()),
                    new TokenAuth(incomingKeyStone.getId(), openStackProperties.getProjectId(), openStackProperties.getProjectId()));
            setKeyStone(incomingKeyStone);
            return true;
        } catch (Exception ex) {
            log.error("[Openstack Client] Refresh token request with username/pass authentication error.", ex);
            throw ex;
        }
    }

    public boolean authenticateApplicationCredential(ApplicationCredentialProp applicationCredential) {
        try {
            log.info("Started to refresh auth token with app credential authentication");

            AuthAppCredential auth = AuthAppCredential.builder()
                    .identity(Identity.builder()
                            .methods(new String[]{"application_credential"})
                            .applicationCredential(
                                    AppCredential.builder()
                                            .id(openStackProperties.getApplicationCredential().getCredentialId())
                                            .secret(openStackProperties.getApplicationCredential().getSecret()).build()
                            ).build())
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AuthAppCredential> httpRequest = new HttpEntity<>(auth, headers);
            ResponseEntity<KeystoneToken> keystoneTokenResponse =
                    openstackRestTemplate.postForEntity(openStackProperties.getAuthHost(), httpRequest, KeystoneToken.class);

            keystoneTokenResponse.getBody().setId(keystoneTokenResponse.getHeaders().getFirst("x-subject-token"));
            KeystoneToken incomingKeyStone = keystoneTokenResponse.getBody();
            log.info("Finished refreshing auth token with app credential authentication: {}", incomingKeyStone);
            incomingKeyStone.applyContext(openStackProperties.getObjectStorageHost().concat(openStackProperties.getMeta().getSwiftBasePath()),
                    new TokenAuth(incomingKeyStone.getId(), openStackProperties.getProjectId(), openStackProperties.getProjectId()));
            setKeyStone(incomingKeyStone);
            return true;
        } catch (Exception ex) {
            log.error("[Openstack Client] Refresh token request with app credential authentication error.", ex);
            throw ex;
        }
    }

    public KeystoneToken getKeyStone() {
        return keystoneTokenAtomic.get();
    }

    public void setKeyStone(KeystoneToken keystoneToken) {
        keystoneTokenAtomic.set(keystoneToken);
    }
}
