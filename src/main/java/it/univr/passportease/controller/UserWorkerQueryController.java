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

@Controller
@AllArgsConstructor
public class UserWorkerQueryController {

    private final UserWorkerQueryService userWorkerQueryService;

    private BucketLimiter bucketLimiter;

    @QueryMapping
    public List<Availability> getAvailabilities(@Argument("availabilityFilters") AvailabilityFilters availabilityFilters, @Argument("size") Integer size, @Argument("page") Integer page) throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_AVAILABILITIES);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many getAvailabilities attempts");

        return userWorkerQueryService.getAvailabilities(availabilityFilters, page, size);
    }

    @QueryMapping
    public List<Office> getOffices() throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_OFFICES);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many getOffices attempts");

        return userWorkerQueryService.getOffices();
    }
}
