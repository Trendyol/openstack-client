package com.trendyol.openstack.client.model.client.auth.password;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsernamePasswordCredential {
    private User user;

    public record User(String id, String password) {
    }
}
