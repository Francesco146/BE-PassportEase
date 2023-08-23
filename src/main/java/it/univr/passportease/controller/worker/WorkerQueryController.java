package it.univr.passportease.controller.worker;

import io.github.bucket4j.Bucket;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
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
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return workerQueryService.getRequestByAvailabilityID(id);
        else throw new RateLimitException("Too many getRequestsByAvailabilityId attempts");
    }

    @QueryMapping
    public List<RequestType> getAllRequestTypes() throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return workerQueryService.getAllRequestTypes();
        else throw new RateLimitException("Too many getAllRequestTypes attempts");
    }
}
