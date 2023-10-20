package it.univr.passportease.exception.notfound;

/**
 * This exception is thrown when a notification requested by id is not found.
 */
public class NotificationNotFoundException extends IllegalArgumentException {
    /**
     * @param message The message of the exception.
     */
    public NotificationNotFoundException(String message) {
        super(message);
    }
}
