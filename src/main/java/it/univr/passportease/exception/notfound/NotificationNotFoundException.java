package it.univr.passportease.exception.notfound;

/**
 * This exception is thrown when a notification requested by id is not found.
 */
public class NotificationNotFoundException extends IllegalArgumentException {
    /**
     * Constructs a {@code NotificationNotFoundException} with no detail message.
     *
     * @param message The message of the exception.
     */
    public NotificationNotFoundException(String message) {
        super(message);
    }
}
