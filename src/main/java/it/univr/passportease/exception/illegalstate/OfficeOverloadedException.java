package it.univr.passportease.exception.illegalstate;

public class OfficeOverloadedException extends RuntimeException {
    public OfficeOverloadedException(String message) {
        super(message);
    }
}
