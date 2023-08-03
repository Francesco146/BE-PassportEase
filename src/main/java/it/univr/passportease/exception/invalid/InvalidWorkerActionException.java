package it.univr.passportease.exception.invalid;

public class InvalidWorkerActionException extends IllegalArgumentException {
    public InvalidWorkerActionException(String message) {
        super(message);
    }
}
