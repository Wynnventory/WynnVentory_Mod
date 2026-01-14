package com.wynnventory.api.exception;

public final class ApiKeyException extends RuntimeException {
    public ApiKeyException(String message) {
        super(message);
    }

    public ApiKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
