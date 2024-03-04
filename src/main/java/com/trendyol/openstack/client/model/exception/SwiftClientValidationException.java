package com.trendyol.openstack.client.model.exception;

import lombok.Builder;

public class SwiftClientValidationException extends RuntimeException {
    @Builder
    public SwiftClientValidationException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
