package it.univr.passportease.controller.worker;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.RequestNotFoundException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.helper.ratelimiter.RateLimiter;
import it.univr.passportease.service.worker.WorkerMutationService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * Controller for worker mutations. It handles the following GraphQL mutations:
 * <ul>
 *     <li>{@link WorkerMutationController#createRequest(RequestInput)}</li>
 *     <li>{@link WorkerMutationController#modifyRequest(String, RequestInput)}</li>
 *     <li>{@link WorkerMutationController#deleteRequest(String)}</li>
 * </ul>
 *
 * @see WorkerMutationService
 */
@Controller
@AllArgsConstructor
public class WorkerMutationController {

    /**
     * Worker mutation service.
     */
    private final WorkerMutationService workerMutationService;

    /**
     * Bucket limiter.
     */
    private BucketLimiter bucketLimiter;
    /**
     * Request analyzer.
     */
    private RequestAnalyzer requestAnalyzer;

    /**
     * This mutation creates a request. It returns the created request.
     *
     * @param requestInput {@link RequestInput} containing the request's data
     * @return the created {@link Request}
     * @throws AuthenticationCredentialsNotFoundException if the worker has not inserted the token in the request
     * @throws WorkerNotFoundException                    if the worker is not found
     * @throws InvalidRequestTypeException                if the request type is invalid
     * @throws OfficeOverloadedException                  if the office is overloaded and cannot accept more requests
     * @throws RateLimitException                         if the worker has exceeded the rate limit
     */
    @MutationMapping
    public Request createRequest(@Argument("request") RequestInput requestInput)
            throws AuthenticationCredentialsNotFoundException, WorkerNotFoundException, InvalidRequestTypeException, OfficeOverloadedException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.CREATE_REQUEST);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many createRequest attempts");

        return workerMutationService.createRequest(requestAnalyzer.getTokenFromRequest(), requestInput);
    }

    /**
     * This mutation modifies a request. It returns the modified request.
     *
     * @param requestID    the request's ID
     * @param requestInput {@link RequestInput} containing the request's data
     * @return the modified {@link Request}
     * @throws WorkerNotFoundException                    if the worker is not found
     * @throws RequestNotFoundException                   if the request is not found
     * @throws OfficeOverloadedException                  if the office is overloaded and cannot accept more requests
     * @throws AuthenticationCredentialsNotFoundException if the worker has not inserted the token in the request
     * @throws RateLimitException                         if the worker has exceeded the rate limit
     */
    @MutationMapping
    public Request modifyRequest(@Argument("requestID") String requestID, @Argument("request") RequestInput requestInput)
            throws WorkerNotFoundException, RequestNotFoundException, OfficeOverloadedException, RateLimitException, AuthenticationCredentialsNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.MODIFY_REQUEST);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many modifyRequest attempts");

        return workerMutationService.modifyRequest(requestAnalyzer.getTokenFromRequest(), requestID, requestInput);
    }

    /**
     * This mutation deletes a request. It returns nothing.
     *
     * @param requestID the request's ID
     * @throws WorkerNotFoundException                    if the worker is not found
     * @throws RequestNotFoundException                   if the request is not found
     * @throws AuthenticationCredentialsNotFoundException if the worker has not inserted the token in the request
     * @throws RateLimitException                         if the worker has exceeded the rate limit
     */
    @MutationMapping
    public void deleteRequest(@Argument("requestID") String requestID)
            throws WorkerNotFoundException, RequestNotFoundException, RateLimitException, AuthenticationCredentialsNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.DELETE_REQUEST);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many deleteRequest attempts");

        workerMutationService.deleteRequest(requestAnalyzer.getTokenFromRequest(), requestID);
    }
}
