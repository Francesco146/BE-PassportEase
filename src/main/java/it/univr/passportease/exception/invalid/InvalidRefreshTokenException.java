package it.univr.passportease.exception.invalid;

public class InvalidRefreshTokenException extends IllegalArgumentException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
