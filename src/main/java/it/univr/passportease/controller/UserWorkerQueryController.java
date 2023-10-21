package it.univr.passportease.controller;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.input.AvailabilityFilters;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Office;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.helper.ratelimiter.RateLimiter;
import it.univr.passportease.service.userworker.UserWorkerQueryService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller for user and worker queries. It handles the following GraphQL queries:
 * <ul>
 *     <li>{@link UserWorkerQueryController#getAvailabilities(AvailabilityFilters, Integer, Integer)}</li>
 *     <li>{@link UserWorkerQueryController#getOffices()}</li>
 * </ul>
 *
 * @see UserWorkerQueryService
 */
@Controller
@AllArgsConstructor
public class UserWorkerQueryController {

    /**
     * Service for user and worker queries.
     */
    private final UserWorkerQueryService userWorkerQueryService;

    /**
     * Bucket limiter for rate limiting.
     */
    private BucketLimiter bucketLimiter;

    /**
     * This query returns the availabilities matching the given filters. It returns a paginated list of availabilities if
     * the page and size arguments are provided.
     *
     * @param availabilityFilters filters to apply to the query, if any
     * @param size                number of results to return per page, optional
     * @param page                page number starting from 0, optional
     * @return list of {@link Availability} matching the filters, paginated if requested
     * @throws RateLimitException if the user or worker has exceeded the rate limit
     */
    @QueryMapping
    public List<Availability> getAvailabilities(@Argument("availabilityFilters") AvailabilityFilters availabilityFilters, @Argument("size") Integer size, @Argument("page") Integer page) throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_AVAILABILITIES);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many getAvailabilities attempts");

        return userWorkerQueryService.getAvailabilities(availabilityFilters, page, size);
    }

    /**
     * This query returns all the offices in the database.
     *
     * @return list of {@link Office}
     * @throws RateLimitException if the user or worker has exceeded the rate limit
     */
    @QueryMapping
    public List<Office> getOffices() throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_OFFICES);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many getOffices attempts");

        return userWorkerQueryService.getOffices();
    }
}
