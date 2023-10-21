package it.univr.passportease.exception.notfound;

/**
 * Exception thrown when a worker is not found.
 */
public class WorkerNotFoundException extends IllegalArgumentException {
    /**
     * Default constructor, it sets the message to "Worker not found.".
     */
    public WorkerNotFoundException() {
        this("Worker not found.");
    }

    /**
     * Constructor for the {@link WorkerNotFoundException}
     *
     * @param message The message of the exception.
     */
    public WorkerNotFoundException(String message) {
        super(message);
    }
}
