package com.trendyol.openstack.client.model.prop.openstack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebClientProp {
    private Integer connectTimeoutMillis = 20000;
    private Integer readTimeoutMillis = 20000;
}
