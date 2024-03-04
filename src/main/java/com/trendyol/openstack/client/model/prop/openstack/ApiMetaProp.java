package com.trendyol.openstack.client.model.prop.openstack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiMetaProp {
    private String authTokenKey;
    private String tempUrlKeyHeader;
    private String tempUrlKeyHeader2;
    private String swiftBasePath;
    private String delimeter;
    private Integer setTempKeyCycleMinutes;
}
