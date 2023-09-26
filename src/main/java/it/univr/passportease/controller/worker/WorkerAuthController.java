package it.univr.passportease.controller.worker;

import io.github.bucket4j.Bucket;
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

@Controller
@AllArgsConstructor
public class WorkerAuthController {
    private final WorkerAuthService workerAuthService;
    BucketLimiter bucketLimiter;

    @MutationMapping
    public LoginOutput loginWorker(@Argument("username") String username, @Argument("password") String password)
            throws WorkerNotFoundException, WrongPasswordException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.LOGIN_WORKER);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many login attempts");

        return workerAuthService.login(username, password);
    }

    @MutationMapping
    public LoginOutput registerWorker(@Argument("workerInput") WorkerInput workerInput)
            throws RateLimitException, OfficeNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.REGISTER_WORKER);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many register attempts");

        return workerAuthService.register(workerInput);
    }
}
