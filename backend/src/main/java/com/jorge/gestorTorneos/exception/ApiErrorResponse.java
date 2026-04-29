package com.jorge.gestorTorneos.exception;

public record ApiErrorResponse(int status, String error, String message, String path) {

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(status, error, message, path);
    }
}
