package it.univr.passportease.exception.security;

/**
 * This exception is thrown when an access token is not found in Redis.
 */
public class TokenNotInRedisException extends SecurityException {
    /**
     * @param message The message of the exception.
     */
    public TokenNotInRedisException(String message) {
        super(message);
    }
}
