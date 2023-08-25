package it.univr.passportease.exception.invalid;

public class InvalidDataFromRequestException extends IllegalArgumentException {
    public InvalidDataFromRequestException(String message) {
        super(message);
    }
}
