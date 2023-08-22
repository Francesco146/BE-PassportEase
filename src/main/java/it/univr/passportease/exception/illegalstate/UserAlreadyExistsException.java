package it.univr.passportease.exception.illegalstate;

public class UserAlreadyExistsException extends IllegalStateException {
    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
