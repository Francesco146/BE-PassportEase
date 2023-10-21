package it.univr.passportease.exception.security;

/**
 * This exception is thrown when the user has exceeded the rate limit.
 */
public class RateLimitException extends IllegalStateException {
    /**
     * Constructor for the {@link RateLimitException}
     *
     * @param message The message of the exception.
     */
    public RateLimitException(String message) {
        super(message);
    }
}
