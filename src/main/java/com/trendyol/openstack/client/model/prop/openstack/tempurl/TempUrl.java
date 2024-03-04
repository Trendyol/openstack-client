package com.trendyol.openstack.client.model.prop.openstack.tempurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempUrl {
    public String tempUrl;
}
