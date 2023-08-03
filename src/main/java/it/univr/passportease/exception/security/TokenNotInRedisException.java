package it.univr.passportease.exception.security;

public class TokenNotInRedisException extends SecurityException {
    public TokenNotInRedisException(String message) {
        super(message);
    }
}
