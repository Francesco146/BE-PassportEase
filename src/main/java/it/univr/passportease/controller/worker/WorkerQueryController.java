package it.univr.passportease.controller.worker;

import io.github.bucket4j.Bucket;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.helper.ratelimiter.RateLimiter;
import it.univr.passportease.service.worker.WorkerQueryService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller for worker queries. It handles the following GraphQL queries:
 * <ul>
 *     <li>{@link WorkerQueryController#getRequestByAvailabilityID(String)}</li>
 *     <li>{@link WorkerQueryController#getAllRequestTypes()}</li>
 * </ul>
 *
 * @see WorkerQueryService
 */
@Controller
@AllArgsConstructor
public class WorkerQueryController {

    /**
     * Worker query service.
     */
    private final WorkerQueryService workerQueryService;

    /**
     * Bucket limiter.
     */
    private BucketLimiter bucketLimiter;

    /**
     * This query returns the request with the given availability id.
     *
     * @param id availability id
     * @return {@link Request} with the given availability id
     * @throws InvalidAvailabilityIDException if the given availability id is invalid
     * @throws RateLimitException             if the rate limit is exceeded
     */
    @QueryMapping
    public Request getRequestByAvailabilityID(@Argument("availabilityID") String id) throws InvalidAvailabilityIDException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_REQUEST_BY_AVAILABILITY_ID);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many getRequestByAvailabilityID attempts");

        return workerQueryService.getRequestByAvailabilityID(id);
    }

    /**
     * This query returns all the request types in the database.
     *
     * @return all the request types in the database
     * @throws RateLimitException if the rate limit is exceeded
     */
    @QueryMapping
    public List<RequestType> getAllRequestTypes() throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_ALL_REQUEST_TYPES);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many getAllRequestTypes attempts");

        return workerQueryService.getAllRequestTypes();
    }
}
