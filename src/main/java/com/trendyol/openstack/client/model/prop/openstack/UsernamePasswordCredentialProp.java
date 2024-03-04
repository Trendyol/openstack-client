package com.trendyol.openstack.client.model.prop.openstack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernamePasswordCredentialProp {
    private String id;
    private String password;
}
