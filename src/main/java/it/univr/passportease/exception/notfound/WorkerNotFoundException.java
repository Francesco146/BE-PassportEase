package it.univr.passportease.exception.notfound;

import javax.naming.AuthenticationException;

public class WorkerNotFoundException extends AuthenticationException {

    public WorkerNotFoundException(String message) {
        super(message);
    }
}
