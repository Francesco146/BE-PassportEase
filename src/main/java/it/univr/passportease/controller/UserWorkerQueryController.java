package it.univr.passportease.controller;

import io.github.bucket4j.Bucket;
import it.univr.passportease.entity.Office;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.service.UserWorkerQueryService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserWorkerQueryController {
    private final UserWorkerQueryService userWorkerQueryService;
    private BucketLimiter bucketLimiter;

    @QueryMapping
    public void getAvailabilities() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @QueryMapping
    public List<Office> getOffices() throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return userWorkerQueryService.getOffices();
        else
            throw new RateLimitException("Too many getOffices attempts");
    }
}
