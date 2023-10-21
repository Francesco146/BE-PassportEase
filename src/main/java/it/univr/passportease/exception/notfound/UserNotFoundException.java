package it.univr.passportease.exception.notfound;

/**
 * This exception is thrown when a user is not found.
 */
public class UserNotFoundException extends IllegalArgumentException {
    /**
     * Constructor for the {@link UserNotFoundException}
     *
     * @param message The message of the exception.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
