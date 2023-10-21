package it.univr.passportease.service.userworker;

import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.exception.invalid.InvalidEmailException;
import it.univr.passportease.exception.invalid.InvalidRefreshTokenException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.notfound.UserOrWorkerIDNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.TokenNotInRedisException;
import it.univr.passportease.exception.security.WrongPasswordException;
import it.univr.passportease.helper.JWT;

/**
 * Service that handles the mutation of the user and worker data
 */
public interface UserWorkerMutationService {
    /**
     * Logs out the user / worker
     *
     * @throws TokenNotInRedisException                   if the token is not in redis
     * @throws UserNotFoundException                      if the user is not found
     * @throws AuthenticationCredentialsNotFoundException if the authentication credentials are not found in the request
     */
    void logout() throws TokenNotInRedisException, UserNotFoundException, AuthenticationCredentialsNotFoundException;

    /**
     * Refresh the access token, and the refresh token in the database
     *
     * @param token        the access token
     * @param refreshToken the refresh token
     * @return the new JWTSet
     * @throws UserNotFoundException           if the user is not found
     * @throws InvalidRefreshTokenException    if the refresh token is invalid
     * @throws UserOrWorkerIDNotFoundException if the user or worker id is not found
     */
    JWTSet refreshAccessToken(JWT token, JWT refreshToken) throws UserNotFoundException, InvalidRefreshTokenException, UserOrWorkerIDNotFoundException;

    /**
     * Change password in the database
     *
     * @param oldPassword the old password
     * @param newPassword the new password
     * @throws UserNotFoundException                      if the user is not found
     * @throws WrongPasswordException                     if the old password is wrong
     * @throws AuthenticationCredentialsNotFoundException if the authentication credentials are not found in the request
     */
    void changePassword(String oldPassword, String newPassword) throws UserNotFoundException, WrongPasswordException, AuthenticationCredentialsNotFoundException;

    /**
     * Change email in the database
     *
     * @param newEmail the new email
     * @param oldEmail the old email
     * @return the new email
     * @throws UserNotFoundException                      if the user is not found
     * @throws AuthenticationCredentialsNotFoundException if the authentication credentials are not found in the request
     * @throws InvalidEmailException                      if the email is invalid
     */
    String changeEmail(String newEmail, String oldEmail) throws UserNotFoundException, AuthenticationCredentialsNotFoundException, InvalidEmailException;
}
