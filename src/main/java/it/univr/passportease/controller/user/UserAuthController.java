package it.univr.passportease.controller.user;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.exception.illegalstate.UserAlreadyExistsException;
import it.univr.passportease.exception.invalid.InvalidEmailException;
import it.univr.passportease.exception.invalid.InvalidRefreshTokenException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.notfound.UserOrWorkerIDNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.exception.security.TokenNotInRedisException;
import it.univr.passportease.exception.security.WrongPasswordException;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.helper.ratelimiter.RateLimiter;
import it.univr.passportease.service.user.UserAuthService;
import it.univr.passportease.service.userworker.UserWorkerMutationService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * Controller for user authentication and registration. It handles the following GraphQL mutations:
 * <ul>
 *     <li>{@link UserAuthController#loginUser(String, String)}</li>
 *     <li>{@link UserAuthController#logout()}</li>
 *     <li>{@link UserAuthController#registerUser(RegisterInput)}</li>
 *     <li>{@link UserAuthController#refreshAccessToken(String)}</li>
 *     <li>{@link UserAuthController#changePassword(String, String)}</li>
 *     <li>{@link UserAuthController#changeEmail(String, String)}</li>
 * </ul>
 *
 * @see UserAuthService
 * @see UserWorkerMutationService
 */
@Controller
@AllArgsConstructor
@Log4j2
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final UserWorkerMutationService userWorkerMutationService;

    private final BucketLimiter bucketLimiter;
    private RequestAnalyzer requestAnalyzer;


    /**
     * This mutation logs in the user and returns the access token and the refresh token. By logging in,
     * the user is also registered in the redis cache.
     *
     * @param fiscalCode user's fiscal code
     * @param password   user's password
     * @return {@link LoginOutput} containing the access token and the refresh token
     * @throws UserNotFoundException  if the user is not found
     * @throws WrongPasswordException if the password is wrong
     * @throws RateLimitException     if the user has exceeded the number of login attempts
     */
    @MutationMapping
    public LoginOutput loginUser(@Argument("fiscalCode") String fiscalCode, @Argument("password") String password)
            throws UserNotFoundException, WrongPasswordException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.LOGIN_USER);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many login attempts");

        log.info("Login attempt for user with fiscal code: " + fiscalCode);
        return userAuthService.login(fiscalCode, password);
    }

    /**
     * This mutation invalidates the access token and the refresh token of the user,
     * it is shared between users and workers.
     *
     * @throws TokenNotInRedisException                   if the token is not in redis
     * @throws RateLimitException                         if the user has exceeded the number of logout attempts
     * @throws UserNotFoundException                      if the user is not found
     * @throws AuthenticationCredentialsNotFoundException if the token is not in the request
     */
    @MutationMapping
    public void logout()
            throws TokenNotInRedisException, RateLimitException, UserNotFoundException, AuthenticationCredentialsNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.LOGOUT);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many logout attempts");

        userWorkerMutationService.logout();
    }

    /**
     * This mutation registers the user and returns the access token and the refresh token. By registering,
     * the user is also registered in the redis cache, and the user is logged in.
     *
     * @param registerInput user's data, see {@link RegisterInput}
     * @return {@link LoginOutput} containing the access token and the refresh token
     * @throws RateLimitException         if the user has exceeded the number of register attempts
     * @throws UserNotFoundException      if the user is not found
     * @throws UserAlreadyExistsException if the user already exists
     */
    @MutationMapping
    public LoginOutput registerUser(@Argument("registerInput") RegisterInput registerInput)
            throws RateLimitException, UserNotFoundException, UserAlreadyExistsException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.REGISTER_USER);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many register attempts");

        return userAuthService.register(registerInput);
    }

    /**
     * This mutation refreshes the access token and the refresh token of the user.
     *
     * @param refreshToken the refresh token
     * @return {@link JWTSet} containing the new access token and the new refresh token
     * @throws AuthenticationCredentialsNotFoundException if the token is not in the request
     * @throws UserNotFoundException                      if the user is not found
     * @throws RateLimitException                         if the user has exceeded the number of refresh attempts
     * @throws InvalidRefreshTokenException               if the refresh token is invalid
     * @throws UserOrWorkerIDNotFoundException            if the user or the worker is not found
     */
    @MutationMapping
    public JWTSet refreshAccessToken(@Argument("refreshToken") String refreshToken)
            throws AuthenticationCredentialsNotFoundException, UserNotFoundException, RateLimitException, InvalidRefreshTokenException, UserOrWorkerIDNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.REFRESH_ACCESS_TOKEN);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many refresh attempts");

        return userWorkerMutationService.refreshAccessToken(requestAnalyzer.getTokenFromRequest(), new JWT(refreshToken));
    }

    /**
     * @param oldPassword the old password
     * @param newPassword the new password
     * @throws UserNotFoundException                      if the user is not found
     * @throws AuthenticationCredentialsNotFoundException if the token is not in the request
     * @throws WrongPasswordException                     if the old password is wrong
     * @throws RateLimitException                         if the user has exceeded the number of change password attempts
     */
    @MutationMapping
    public void changePassword(@Argument("oldPassword") String oldPassword, @Argument("newPassword") String newPassword)
            throws UserNotFoundException, AuthenticationCredentialsNotFoundException, WrongPasswordException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.CHANGE_PASSWORD);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many change password attempts");

        userWorkerMutationService.changePassword(oldPassword, newPassword);
    }

    /**
     * @param newEmail the new email
     * @param oldEmail the old email
     * @return the new email
     * @throws UserNotFoundException                      if the user is not found
     * @throws InvalidEmailException                      if the new email is invalid
     * @throws RateLimitException                         if the user has exceeded the number of change email attempts
     * @throws AuthenticationCredentialsNotFoundException if the token is not in the request
     */
    @MutationMapping
    public String changeEmail(@Argument("newEmail") String newEmail, @Argument("oldEmail") String oldEmail)
            throws UserNotFoundException, InvalidEmailException, RateLimitException, AuthenticationCredentialsNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.CHANGE_EMAIL);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many change email attempts");

        return userWorkerMutationService.changeEmail(newEmail, oldEmail);
    }
}
