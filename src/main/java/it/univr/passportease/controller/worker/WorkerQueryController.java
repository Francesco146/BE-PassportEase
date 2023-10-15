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

@Controller
@AllArgsConstructor
public class WorkerQueryController {

    private final WorkerQueryService workerQueryService;

    private BucketLimiter bucketLimiter;

    @QueryMapping
    public Request getRequestByAvailabilityID(@Argument("availabilityID") String id) throws InvalidAvailabilityIDException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_REPORT_DETAILS_BY_AVAILABILITY_ID);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many getRequestByAvailabilityID attempts");

        return workerQueryService.getRequestByAvailabilityID(id);
    }

    @QueryMapping
    public List<RequestType> getAllRequestTypes() throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_ALL_REQUEST_TYPES);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many getAllRequestTypes attempts");

        return workerQueryService.getAllRequestTypes();
    }
}
