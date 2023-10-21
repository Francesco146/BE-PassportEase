package it.univr.passportease.exception.notfound;

/**
 * This exception is thrown when a request is not found.
 */
public class RequestNotFoundException extends IllegalArgumentException {
    /**
     * Constructor for the {@link RequestNotFoundException}
     *
     * @param message The message of the exception.
     */
    public RequestNotFoundException(String message) {
        super(message);
    }
}
