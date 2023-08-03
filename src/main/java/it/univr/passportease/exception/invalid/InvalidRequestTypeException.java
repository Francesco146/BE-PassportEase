package it.univr.passportease.exception.invalid;

public class InvalidRequestTypeException extends IllegalArgumentException {
    public InvalidRequestTypeException(String message) {
        super(message);
    }
}
