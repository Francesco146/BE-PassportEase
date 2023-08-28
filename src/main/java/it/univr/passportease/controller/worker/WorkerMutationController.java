package it.univr.passportease.controller.worker;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.service.worker.WorkerMutationService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WorkerMutationController {
    private final WorkerMutationService workerMutationService;
    private RequestAnalyzer requestAnalyzer;
    private BucketLimiter bucketLimiter;

    @MutationMapping
    public Request createRequest(@Argument("request") RequestInput requestInput)
            throws AuthenticationCredentialsNotFoundException, WorkerNotFoundException, InvalidRequestTypeException, OfficeOverloadedException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return workerMutationService.createRequest(requestAnalyzer.getTokenFromRequest(), requestInput);
        else throw new RateLimitException("Too many createRequest attempts");
    }

    @MutationMapping
    public Request modifyRequest(@Argument("requestID") String requestID, @Argument("request") RequestInput requestInput) {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return workerMutationService.modifyRequest(requestAnalyzer.getTokenFromRequest(), requestID, requestInput);
        else throw new RateLimitException("Too many modifyRequest attempts");
    }

    @MutationMapping
    public void deleteRequest(@Argument("requestID") String requestID) {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            workerMutationService.deleteRequest(requestAnalyzer.getTokenFromRequest(), requestID);
        else throw new RateLimitException("Too many deleteRequest attempts");
    }
}
