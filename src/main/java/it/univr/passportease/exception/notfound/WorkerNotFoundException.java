package it.univr.passportease.exception.notfound;

/**
 * Exception thrown when a worker is not found.
 */
public class WorkerNotFoundException extends IllegalArgumentException {
    /**
     * @param message The message of the exception.
     */

    public WorkerNotFoundException(String message) {
        super(message);
    }
}
