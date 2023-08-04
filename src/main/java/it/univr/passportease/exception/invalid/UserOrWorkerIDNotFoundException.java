package it.univr.passportease.exception.invalid;

public class UserOrWorkerIDNotFoundException extends IllegalArgumentException {
    public UserOrWorkerIDNotFoundException(String message) {
        super(message);
    }
}
