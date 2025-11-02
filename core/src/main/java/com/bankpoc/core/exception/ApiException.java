package com.bankpoc.core.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    // === Constructors ===
    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    // === Static factory methods (for cleaner usage) ===

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }

    public static ApiException internalError(String message) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
