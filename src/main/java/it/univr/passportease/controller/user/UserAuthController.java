package it.univr.passportease.controller.user;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.exception.RateLimitException;
import it.univr.passportease.exception.WrongPasswordException;
import it.univr.passportease.helper.BucketLimiter;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.service.UserWorkerMutationService;
import it.univr.passportease.service.user.UserAuthService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            throws UsernameNotFoundException, WrongPasswordException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return userAuthService.login(fiscalCode, password);
        else throw new RateLimitException("Too many login attempts");
    }

    @MutationMapping
    public void logout() {
        userWorkerMutationService.logout();
    }

    @MutationMapping
    public LoginOutput registerUser(@Argument("registerInput") RegisterInput registerInput) {
        return userAuthService.register(registerInput);
    }

    @MutationMapping
    public JWTSet refreshAccessToken(@Argument("refreshToken") String refreshToken) {
        return userWorkerMutationService.refreshAccessToken(requestAnalyzer.getTokenFromRequest(), refreshToken);
    }

    @MutationMapping
    public void changePassword(@Argument("oldPassword") String oldPassword, @Argument("newPassword") String newPassword) {
        userWorkerMutationService.changePassword(oldPassword, newPassword);
    }

    @MutationMapping
    public String changeEmail(@Argument("newEmail") String newEmail, @Argument("oldEmail") String oldEmail) {
        return userWorkerMutationService.changeEmail(newEmail, oldEmail);
    }
}
