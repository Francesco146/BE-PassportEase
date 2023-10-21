package it.univr.passportease.exception.notfound;

/**
 * This exception is thrown when an office is not found.
 */
public class OfficeNotFoundException extends IllegalArgumentException {
    /**
     * Constructs a {@link OfficeNotFoundException} with no detail message.
     *
     * @param message The message of the exception.
     */
    public OfficeNotFoundException(String message) {
        super(message);
    }
}
