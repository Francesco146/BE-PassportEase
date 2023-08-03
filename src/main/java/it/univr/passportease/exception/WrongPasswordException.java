package it.univr.passportease.exception;

import javax.naming.AuthenticationException;

public class WrongPasswordException extends AuthenticationException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
