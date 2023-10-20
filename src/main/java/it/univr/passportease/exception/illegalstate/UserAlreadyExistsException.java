package it.univr.passportease.exception.illegalstate;

/**
 * Exception thrown when a user already exists in the database.
 */
public class UserAlreadyExistsException extends IllegalStateException {
    /**
     * @param msg the detail message.
     */
    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
