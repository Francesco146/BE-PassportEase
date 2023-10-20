package it.univr.passportease.exception.notfound;

/**
 * This exception is thrown when a user / worker ID is not found in the database.
 */
public class UserOrWorkerIDNotFoundException extends IllegalArgumentException {
    /**
     * @param message The message of the exception.
     */
    public UserOrWorkerIDNotFoundException(String message) {
        super(message);
    }
}
