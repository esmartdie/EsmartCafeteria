package com.esmartdie.EsmartCafeteriaApi.exception;


public class SessionActiveNotFoundException extends RuntimeException {
    public SessionActiveNotFoundException(String message) {
        super(message);
    }

    public SessionActiveNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}