package com.trendyol.openstack.client.model.client.auth.appcredential;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("auth")
public class AuthAppCredential {
    private Identity identity;
}
