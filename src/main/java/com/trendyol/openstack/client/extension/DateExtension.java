package com.trendyol.openstack.client.extension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public interface DateExtension {
    default LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
