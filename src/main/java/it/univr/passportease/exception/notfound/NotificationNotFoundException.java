package it.univr.passportease.exception.notfound;

public class NotificationNotFoundException extends IllegalArgumentException {
    public NotificationNotFoundException(String message) {
        super(message);
    }
}
