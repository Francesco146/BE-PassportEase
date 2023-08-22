package it.univr.passportease.controller.user;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.exception.illegalstate.UserAlreadyExistsException;
import it.univr.passportease.exception.invalid.InvalidEmailException;
import it.univr.passportease.exception.invalid.InvalidRefreshTokenException;
import it.univr.passportease.exception.invalid.UserOrWorkerIDNotFoundException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.exception.security.TokenNotInRedisException;
import it.univr.passportease.exception.security.WrongPasswordException;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.service.UserWorkerMutationService;
import it.univr.passportease.service.user.UserAuthService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final UserWorkerMutationService userWorkerMutationService;
    private final BucketLimiter bucketLimiter;

    private RequestAnalyzer requestAnalyzer;


    @MutationMapping
    public LoginOutput loginUser(@Argument("fiscalCode") String fiscalCode, @Argument("password") String password)
            throws UserNotFoundException, WrongPasswordException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return userAuthService.login(fiscalCode, password);
        else throw new RateLimitException("Too many login attempts");
    }

    @MutationMapping
    public void logout()
            throws TokenNotInRedisException, RateLimitException, UserNotFoundException, AuthenticationCredentialsNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            userWorkerMutationService.logout();
        else throw new RateLimitException("Too many logout attempts");
    }

    @MutationMapping
    public LoginOutput registerUser(@Argument("registerInput") RegisterInput registerInput)
            throws RateLimitException, UserNotFoundException, UserAlreadyExistsException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return userAuthService.register(registerInput);
        else throw new RateLimitException("Too many register attempts");
    }

    @MutationMapping
    public JWTSet refreshAccessToken(@Argument("refreshToken") String refreshToken)
            throws AuthenticationCredentialsNotFoundException, UserNotFoundException, RateLimitException, InvalidRefreshTokenException, UserOrWorkerIDNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return userWorkerMutationService.refreshAccessToken(requestAnalyzer.getTokenFromRequest(), refreshToken);
        else throw new RateLimitException("Too many refresh attempts");
    }

    @MutationMapping
    public void changePassword(@Argument("oldPassword") String oldPassword, @Argument("newPassword") String newPassword)
            throws UserNotFoundException, AuthenticationCredentialsNotFoundException, WrongPasswordException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            userWorkerMutationService.changePassword(oldPassword, newPassword);
        else throw new RateLimitException("Too many change password attempts");
    }

    @MutationMapping
    public String changeEmail(@Argument("newEmail") String newEmail, @Argument("oldEmail") String oldEmail)
            throws UserNotFoundException, InvalidEmailException, RateLimitException, AuthenticationCredentialsNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return userWorkerMutationService.changeEmail(newEmail, oldEmail);
        else throw new RateLimitException("Too many change email attempts");
    }
}
