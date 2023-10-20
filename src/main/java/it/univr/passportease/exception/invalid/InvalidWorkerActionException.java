package it.univr.passportease.exception.invalid;

/**
 * Exception thrown when a worker tries to perform an invalid or not allowed action.
 */
public class InvalidWorkerActionException extends IllegalArgumentException {
    /**
     * @param message The message of the exception.
     */
    public InvalidWorkerActionException(String message) {
        super(message);
    }
}
