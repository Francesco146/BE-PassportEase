package it.univr.passportease.exception.invalid;

/**
 * Exception thrown when the generic data from the request are invalid.
 */
public class InvalidDataFromRequestException extends IllegalArgumentException {
    /**
     * @param message the detail message.
     */
    public InvalidDataFromRequestException(String message) {
        super(message);
    }
}
