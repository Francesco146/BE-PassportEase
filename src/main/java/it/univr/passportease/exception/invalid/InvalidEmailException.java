package it.univr.passportease.exception.invalid;

/**
 * Exception thrown when an email is invalid.
 */
public class InvalidEmailException extends IllegalArgumentException {
    /**
     * @param message The message of the exception.
     */
    public InvalidEmailException(String message) {
        super(message);
    }
}
