package it.univr.passportease.exception.notfound;

/**
 * This exception is thrown when an availability is not found.
 */
public class AvailabilityNotFoundException extends IllegalArgumentException {
    /**
     * @param message The message of the exception.
     */
    public AvailabilityNotFoundException(String message) {
        super(message);
    }
}
