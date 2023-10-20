package it.univr.passportease.controller.worker;

import io.github.bucket4j.Bucket;
import it.univr.passportease.controller.user.UserAuthController;
import it.univr.passportease.dto.input.WorkerInput;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.exception.notfound.OfficeNotFoundException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.exception.security.WrongPasswordException;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.helper.ratelimiter.RateLimiter;
import it.univr.passportease.service.worker.WorkerAuthService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * Controller for worker authentication and registration. It handles the following GraphQL mutations:
 * <ul>
 *     <li>{@link WorkerAuthController#loginWorker(String, String)}</li>
 *     <li>{@link WorkerAuthController#registerWorker(WorkerInput)}</li>
 * </ul>
 * <p>
 * The other mutations are handled by {@link UserAuthController} as they are common to both users and workers.
 *
 * @see WorkerAuthService
 * @see UserAuthController
 */
@Controller
@AllArgsConstructor
public class WorkerAuthController {

    private final WorkerAuthService workerAuthService;

    private BucketLimiter bucketLimiter;

    /**
     * @param username worker's username
     * @param password worker's password
     * @return {@link LoginOutput} containing the access token and the refresh token
     * @throws WorkerNotFoundException if the worker is not found
     * @throws WrongPasswordException  if the password is wrong
     * @throws RateLimitException      if the rate limit is exceeded
     */
    @MutationMapping
    public LoginOutput loginWorker(@Argument("username") String username, @Argument("password") String password)
            throws WorkerNotFoundException, WrongPasswordException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.LOGIN_WORKER);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many login attempts");

        return workerAuthService.login(username, password);
    }

    /**
     * The worker is automatically registered by a system administrator.
     *
     * @param workerInput {@link WorkerInput} containing the worker's data
     * @return {@link LoginOutput} containing the access token and the refresh token
     * @throws RateLimitException      if the rate limit is exceeded
     * @throws OfficeNotFoundException if the office is not found
     * @deprecated . Use {@link WorkerAuthController#loginWorker(String, String)} instead.
     */
    @MutationMapping
    @Deprecated(since = "0.0.1")
    public LoginOutput registerWorker(@Argument("workerInput") WorkerInput workerInput)
            throws RateLimitException, OfficeNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.REGISTER_WORKER);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many register attempts");

        return workerAuthService.register(workerInput);
    }
}
