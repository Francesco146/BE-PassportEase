package it.univr.passportease.exception.invalid;

/**
 * This exception is thrown when a generic request type is not valid.
 */
public class InvalidRequestTypeException extends IllegalArgumentException {
    /**
     * @param message The message of the exception.
     */
    public InvalidRequestTypeException(String message) {
        super(message);
    }
}
