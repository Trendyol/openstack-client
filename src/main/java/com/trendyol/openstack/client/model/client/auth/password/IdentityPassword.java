package com.trendyol.openstack.client.model.client.auth.password;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityPassword {

    private String[] methods;

    @JsonProperty("password")
    private UsernamePasswordCredential user;

}
