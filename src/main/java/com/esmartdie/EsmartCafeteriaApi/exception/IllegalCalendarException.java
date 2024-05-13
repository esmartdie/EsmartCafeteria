package com.esmartdie.EsmartCafeteriaApi.exception;

public class IllegalCalendarException extends RuntimeException {
    public IllegalCalendarException(String message) {
        super(message);
    }
    public IllegalCalendarException(String message, Throwable cause) {
        super(message, cause);
    }
}
