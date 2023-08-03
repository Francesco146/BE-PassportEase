package it.univr.passportease.exception;

public class RateLimitException extends SecurityException {
    public RateLimitException(String message) {
        super(message);
    }
}
