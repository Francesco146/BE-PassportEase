package it.univr.passportease.exception.security;

import javax.naming.AuthenticationException;

public class WrongPasswordException extends AuthenticationException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
