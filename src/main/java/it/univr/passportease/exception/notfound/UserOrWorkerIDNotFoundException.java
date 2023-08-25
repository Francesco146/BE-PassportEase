package it.univr.passportease.exception.notfound;

public class UserOrWorkerIDNotFoundException extends IllegalArgumentException {
    public UserOrWorkerIDNotFoundException(String message) {
        super(message);
    }
}
