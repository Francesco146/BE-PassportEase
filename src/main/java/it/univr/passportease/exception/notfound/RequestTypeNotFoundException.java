package it.univr.passportease.exception.notfound;

/**
 * This exception is thrown when a user tries to access a request type that does not exist.
 */
public class RequestTypeNotFoundException extends IllegalArgumentException {
    /**
     * @param message The message of the exception.
     */
    public RequestTypeNotFoundException(String message) {
        super(message);
    }
}
