package it.univr.passportease.service.userworker.impl;

import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.exception.invalid.InvalidEmailException;
import it.univr.passportease.exception.invalid.InvalidRefreshTokenException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.notfound.UserOrWorkerIDNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.TokenNotInRedisException;
import it.univr.passportease.exception.security.WrongPasswordException;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.helper.UserType;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.repository.WorkerRepository;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.userworker.UserWorkerMutationService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Implementation of {@link UserWorkerMutationService}. This class contains all the methods that can be used to
 * mutate user and worker data.
 */
@Service
@AllArgsConstructor
public class UserWorkerMutationServiceImpl implements UserWorkerMutationService {
    /**
     * The regex used to check if the email is valid.
     */
    private static final Pattern EMAIL_VALID_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * The repository for {@link Worker} entity.
     */
    private final WorkerRepository workerRepository;
    /**
     * The repository for {@link User} entity.
     */
    private final UserRepository userRepository;

    /**
     * The service that handles the JWT.
     */
    private final JwtService jwtService;

    /**
     * The service that analyzes the request.
     */
    private RequestAnalyzer requestAnalyzer;
    /**
     * The service that encodes the password.
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Logs out the user by invalidating the access token and the refresh token.
     *
     * @throws UserNotFoundException                      if the user is not found
     * @throws TokenNotInRedisException                   if the token is not in redis
     * @throws AuthenticationCredentialsNotFoundException if the authentication credentials are not found
     */
    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public void logout() throws UserNotFoundException, TokenNotInRedisException, AuthenticationCredentialsNotFoundException {
        JWT accessToken = requestAnalyzer.getTokenFromRequest();
        // TODO: throw exception if refresh token is not in redis
        jwtService.invalidateRefreshToken(accessToken);
        if (!jwtService.invalidateAccessToken(accessToken).equals(Boolean.TRUE))
            throw new TokenNotInRedisException("Error while invalidating access token");
    }

    /**
     * Refreshes the access token and the refresh token.
     * TODO: check if the old Access token will be invalidated
     *
     * @param token        JWT Access token
     * @param refreshToken JWT Refresh token
     * @return New JWT access token and refresh token
     * @throws UserNotFoundException           if the user is not found
     * @throws InvalidRefreshTokenException    if the refresh token is invalid
     * @throws UserOrWorkerIDNotFoundException if the user or worker ID is not found
     */
    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public JWTSet refreshAccessToken(JWT token, JWT refreshToken) throws UserNotFoundException, InvalidRefreshTokenException, UserOrWorkerIDNotFoundException {

        UUID id = jwtService.extractId(token);
        if (id == null)
            throw new UserOrWorkerIDNotFoundException("Token has been corrupted token.id is null");

        UserType userOrWorker = jwtService.getUserOrWorkerFromToken(token);

        if (!userOrWorker.getRefreshToken().equals(refreshToken))
            throw new InvalidRefreshTokenException("Invalid refresh token");

        JWT newRefreshToken = jwtService.generateRefreshToken(id);

        userOrWorker.setRefreshToken(newRefreshToken);

        switch (userOrWorker) {
            case User user -> userRepository.save(user);
            case Worker worker -> workerRepository.save(worker);
            default -> throw new UserNotFoundException("User or worker not found");
        }

        return new JWTSet(jwtService.generateAccessToken(id).getToken(), newRefreshToken.getToken());
    }

    /**
     * Changes the password of the user or worker.
     *
     * @param oldPassword old password
     * @param newPassword new password
     * @throws UserNotFoundException                      if the user is not found
     * @throws WrongPasswordException                     if the old password is wrong
     * @throws AuthenticationCredentialsNotFoundException if the authentication credentials are not found
     */
    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public void changePassword(String oldPassword, String newPassword)
            throws UserNotFoundException, WrongPasswordException, AuthenticationCredentialsNotFoundException {

        JWT accessToken = requestAnalyzer.getTokenFromRequest();
        UserType userOrWorker = jwtService.getUserOrWorkerFromToken(accessToken);

        if (!passwordEncoder.matches(oldPassword, userOrWorker.getHashPassword()))
            throw new WrongPasswordException("Wrong old password");

        userOrWorker.setHashPassword(passwordEncoder.encode(newPassword));

        switch (userOrWorker) {
            case User user -> userRepository.save(user);
            case Worker worker -> workerRepository.save(worker);
            default -> throw new UserNotFoundException("User or worker not found");
        }
    }

    /**
     * Changes the email of the user or worker.
     *
     * @param newEmail new email
     * @param oldEmail old email
     * @return new email
     * @throws UserNotFoundException                      if the user is not found
     * @throws InvalidEmailException                      if the email is invalid
     * @throws AuthenticationCredentialsNotFoundException if the authentication credentials are not found
     */
    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public String changeEmail(String newEmail, String oldEmail)
            throws UserNotFoundException, InvalidEmailException, AuthenticationCredentialsNotFoundException {
        JWT accessToken = requestAnalyzer.getTokenFromRequest();

        if (!emailValid(newEmail))
            throw new InvalidEmailException("Invalid email");

        UserType userOrWorker = jwtService.getUserOrWorkerFromToken(accessToken);

        if (!userOrWorker.getEmail().equals(oldEmail))
            throw new InvalidEmailException("Invalid old email");

        userOrWorker.setEmail(newEmail);

        switch (userOrWorker) {
            case User user -> userRepository.save(user);
            case Worker worker -> workerRepository.save(worker);
            default -> throw new UserNotFoundException("User or worker not found");
        }

        return newEmail;
    }

    /**
     * Checks if the email is valid.
     *
     * @param email email
     * @return true if the email is valid, false otherwise
     */
    private boolean emailValid(String email) {
        return EMAIL_VALID_REGEX
                .matcher(email)
                .matches();
    }
}
