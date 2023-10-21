package it.univr.passportease.exception.security;

/**
 * This exception is thrown when the credentials are not found in the request made to the server.
 */
public class AuthenticationCredentialsNotFoundException extends SecurityException {
    /**
     * Constructor of {@link AuthenticationCredentialsNotFoundException}.
     *
     * @param message The message of the exception.
     */
    public AuthenticationCredentialsNotFoundException(String message) {
        super(message);
    }
}
