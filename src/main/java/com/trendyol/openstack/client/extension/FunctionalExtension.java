package com.trendyol.openstack.client.extension;

import com.trendyol.openstack.client.model.exception.SwiftClientException;
import com.trendyol.openstack.client.model.exception.SwiftClientValidationException;

import java.util.function.Supplier;

public interface FunctionalExtension {

    default <T> T process(Supply<T> func) {
        try {
            return func.process();
        } catch (Exception ex) {
            throw SwiftClientException.builder().message(ex.getMessage()).cause(ex).build();
        }
    }

    default Supplier<RuntimeException> throwExceptionSupplier(String message) {
        return () -> {
            SwiftClientValidationException swiftClientException = SwiftClientValidationException.builder().message(message).build();
            swiftClientException.setStackTrace(new StackTraceElement[0]);
            return swiftClientException;
        };
    }

    default Runnable throwExceptionRunnable(String message) {
        return () -> {
            SwiftClientValidationException swiftClientException = SwiftClientValidationException.builder().message(message).build();
            swiftClientException.setStackTrace(new StackTraceElement[0]);
            throw swiftClientException;
        };
    }

    @FunctionalInterface
    interface Supply<T> {
        T process();
    }

    @FunctionalInterface
    interface SupplyVoid {
        void process();
    }
}
