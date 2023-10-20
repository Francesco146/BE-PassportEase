package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.exception.illegalstate.UserAlreadyExistsException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.security.WrongPasswordException;

/**
 * Service that provides the methods to authenticate the user.
 */
public interface UserAuthService {
    /**
     * @param fiscalCode The fiscal code of the user
     * @param password   The password of the user
     * @return The JWTs of the user
     * @throws UserNotFoundException  if the user is not found
     * @throws WrongPasswordException if the password is wrong
     */
    LoginOutput login(String fiscalCode, String password) throws UserNotFoundException, WrongPasswordException;

    /**
     * @param registerInput The input of the register mutation
     * @return The JWTs of the user
     * @throws UserNotFoundException      if the user is not found
     * @throws UserAlreadyExistsException if the user already exists
     */
    LoginOutput register(RegisterInput registerInput) throws UserNotFoundException, UserAlreadyExistsException;
}
