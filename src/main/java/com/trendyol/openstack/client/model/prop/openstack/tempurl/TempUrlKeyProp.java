package com.trendyol.openstack.client.model.prop.openstack.tempurl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TempUrlKeyProp {
    private String region;
    private String key1;
    private String key2;
}
