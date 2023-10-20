package it.univr.passportease.exception.security;


/**
 * This exception is thrown when the user has entered a wrong password.
 */
public class WrongPasswordException extends IllegalArgumentException {
    /**
     * @param message The message of the exception.
     */
    public WrongPasswordException(String message) {
        super(message);
    }
}
