package com.trendyol.openstack.client.model.client.auth.password;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.trendyol.openstack.client.model.client.auth.Scope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("auth")
public class AuthPassword {

    private IdentityPassword identity;
    private Scope scope;
}
