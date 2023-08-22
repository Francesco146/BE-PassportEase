package it.univr.passportease.exception.security;

public class AuthenticationCredentialsNotFoundException extends SecurityException {
    public AuthenticationCredentialsNotFoundException(String msg) {
        super(msg);
    }
}
