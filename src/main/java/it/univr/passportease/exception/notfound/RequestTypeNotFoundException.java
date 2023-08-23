package it.univr.passportease.exception.notfound;

public class RequestTypeNotFoundException extends IllegalArgumentException {
    public RequestTypeNotFoundException(String message) {
        super(message);
    }
}
