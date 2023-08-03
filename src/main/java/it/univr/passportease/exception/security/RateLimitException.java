package it.univr.passportease.exception.security;

public class RateLimitException extends SecurityException {
    public RateLimitException(String message) {
        super(message);
    }
}
