package it.univr.passportease.exception.invalid;

/**
 * This exception is thrown when a refresh token is invalid.
 */
public class InvalidRefreshTokenException extends IllegalArgumentException {
    /**
     * Constructor of {@link InvalidRefreshTokenException}.
     *
     * @param message The message of the exception.
     */
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
