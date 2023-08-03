package it.univr.passportease.exception.notfound;

public class OfficeNotFoundException extends IllegalArgumentException {
    public OfficeNotFoundException(String message) {
        super(message);
    }
}
