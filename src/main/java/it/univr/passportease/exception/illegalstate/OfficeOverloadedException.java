package it.univr.passportease.exception.illegalstate;

/**
 * Exception thrown when an office is overloaded and cannot accept more requests.
 */
public class OfficeOverloadedException extends RuntimeException {
    /**
     * @param message the detail message.
     */
    public OfficeOverloadedException(String message) {
        super(message);
    }
}
