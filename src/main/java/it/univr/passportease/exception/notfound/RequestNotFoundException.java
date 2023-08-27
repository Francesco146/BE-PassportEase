package it.univr.passportease.exception.notfound;

public class RequestNotFoundException extends IllegalArgumentException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}
