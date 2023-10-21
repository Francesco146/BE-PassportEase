package it.univr.passportease.exception.invalid;

/**
 * Exception thrown when an invalid availability ID is passed to a method.
 */
public class InvalidAvailabilityIDException extends IllegalArgumentException {
    /**
     * Constructs a {@link InvalidAvailabilityIDException} with no detail message.
     *
     * @param message the detail message.
     */
    public InvalidAvailabilityIDException(String message) {
        super(message);
    }
}
