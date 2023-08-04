package it.univr.passportease.service;

import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.exception.invalid.InvalidEmailException;
import it.univr.passportease.exception.invalid.InvalidRefreshTokenException;
import it.univr.passportease.exception.invalid.UserOrWorkerIDNotFoundException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.TokenNotInRedisException;
import it.univr.passportease.exception.security.WrongPasswordException;

public interface UserWorkerMutationService {
    void logout() throws TokenNotInRedisException, UserNotFoundException, AuthenticationCredentialsNotFoundException;

    JWTSet refreshAccessToken(String token, String refreshToken) throws UserNotFoundException, InvalidRefreshTokenException, UserOrWorkerIDNotFoundException;

    void changePassword(String oldPassword, String newPassword) throws UserNotFoundException, WrongPasswordException, AuthenticationCredentialsNotFoundException;

    String changeEmail(String newEmail, String oldEmail) throws UserNotFoundException, AuthenticationCredentialsNotFoundException, InvalidEmailException;
}
