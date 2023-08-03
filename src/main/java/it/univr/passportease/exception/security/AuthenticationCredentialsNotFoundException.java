package it.univr.passportease.exception.security;

import javax.naming.AuthenticationException;

public class AuthenticationCredentialsNotFoundException extends AuthenticationException {
    public AuthenticationCredentialsNotFoundException(String msg) {
        super(msg);
    }
}
