package it.univr.passportease.exception.notfound;

public class AvailabilityNotFoundException extends IllegalArgumentException {
    public AvailabilityNotFoundException(String message) {
        super(message);
    }
}
