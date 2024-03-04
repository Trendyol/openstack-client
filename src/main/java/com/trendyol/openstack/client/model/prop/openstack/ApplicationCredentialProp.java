package com.trendyol.openstack.client.model.prop.openstack;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCredentialProp {
    private String credentialId;
    private String secret;
}
