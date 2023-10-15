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

@Controller
@AllArgsConstructor
@Log4j2
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final UserWorkerMutationService userWorkerMutationService;

    private final BucketLimiter bucketLimiter;
    private RequestAnalyzer requestAnalyzer;


    @MutationMapping
    public LoginOutput loginUser(@Argument("fiscalCode") String fiscalCode, @Argument("password") String password)
            throws UserNotFoundException, WrongPasswordException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.LOGIN_USER);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many login attempts");

        log.info("Login attempt for user with fiscal code: " + fiscalCode);
        return userAuthService.login(fiscalCode, password);
    }

    @MutationMapping
    public void logout()
            throws TokenNotInRedisException, RateLimitException, UserNotFoundException, AuthenticationCredentialsNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.LOGOUT);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many logout attempts");

        userWorkerMutationService.logout();
    }

    @MutationMapping
    public LoginOutput registerUser(@Argument("registerInput") RegisterInput registerInput)
            throws RateLimitException, UserNotFoundException, UserAlreadyExistsException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.REGISTER_USER);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many register attempts");

        return userAuthService.register(registerInput);
    }

    @MutationMapping
    public JWTSet refreshAccessToken(@Argument("refreshToken") String refreshToken)
            throws AuthenticationCredentialsNotFoundException, UserNotFoundException, RateLimitException, InvalidRefreshTokenException, UserOrWorkerIDNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.REFRESH_ACCESS_TOKEN);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many refresh attempts");

        return userWorkerMutationService.refreshAccessToken(requestAnalyzer.getTokenFromRequest(), refreshToken);
    }

    @MutationMapping
    public void changePassword(@Argument("oldPassword") String oldPassword, @Argument("newPassword") String newPassword)
            throws UserNotFoundException, AuthenticationCredentialsNotFoundException, WrongPasswordException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.CHANGE_PASSWORD);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many change password attempts");

        userWorkerMutationService.changePassword(oldPassword, newPassword);
    }

    @MutationMapping
    public String changeEmail(@Argument("newEmail") String newEmail, @Argument("oldEmail") String oldEmail)
            throws UserNotFoundException, InvalidEmailException, RateLimitException, AuthenticationCredentialsNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.CHANGE_EMAIL);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many change email attempts");

        return userWorkerMutationService.changeEmail(newEmail, oldEmail);
    }
}
