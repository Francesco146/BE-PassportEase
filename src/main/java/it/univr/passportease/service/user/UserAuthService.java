package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.exception.illegalstate.UserAlreadyExistsException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.security.WrongPasswordException;

public interface UserAuthService {
    LoginOutput login(String fiscalCode, String password) throws UserNotFoundException, WrongPasswordException;

    LoginOutput register(RegisterInput registerInput) throws UserNotFoundException, UserAlreadyExistsException;
}
