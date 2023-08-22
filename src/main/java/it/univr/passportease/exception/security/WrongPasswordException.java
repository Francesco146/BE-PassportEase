package it.univr.passportease.exception.security;


public class WrongPasswordException extends SecurityException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
