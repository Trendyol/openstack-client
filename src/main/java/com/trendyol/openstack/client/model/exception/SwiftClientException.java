package com.trendyol.openstack.client.model.exception;

import lombok.Builder;

public class SwiftClientException extends RuntimeException {
    @Builder
    public SwiftClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
