package it.univr.passportease.exception.notfound;

public class WorkerNotFoundException extends IllegalArgumentException {

    public WorkerNotFoundException(String message) {
        super(message);
    }
}
