package it.univr.passportease.exception;

public class TokenNotInRedisException extends SecurityException {
    public TokenNotInRedisException(String message) {
        super(message);
    }
}
