package com.trendyol.openstack.client.model.client.auth.appcredential;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppCredential {
    private String id;
    private String secret;
}
