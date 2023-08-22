package it.univr.passportease.controller.user;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.output.ReportDetails;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.service.user.UserQueryService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;
    BucketLimiter bucketLimiter;
    private RequestAnalyzer requestAnalyzer;

    @QueryMapping
    public void getRequestTypesByUser() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @QueryMapping
    public ReportDetails getReportDetailsByAvailabilityId(@Argument("availabilityID") String availabilityId)
            throws SecurityException, InvalidAvailabilityIDException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many get notifications attempts");
        return userQueryService.getReportDetailsByAvailabilityID(availabilityId, requestAnalyzer.getTokenFromRequest());
    }

    @QueryMapping
    public List<Notification> getUserNotifications() throws AuthenticationCredentialsNotFoundException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return userQueryService.getUserNotifications(requestAnalyzer.getTokenFromRequest());
        else throw new RateLimitException("Too many get notifications attempts");
    }

    @QueryMapping
    public User getUserDetails() throws AuthenticationCredentialsNotFoundException, RateLimitException, UserNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return userQueryService.getUserDetails(requestAnalyzer.getTokenFromRequest());
        else throw new RateLimitException("Too many get user details attempts");
    }

    @QueryMapping
    public List<Availability> getUserReservations() throws AuthenticationCredentialsNotFoundException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (bucket.tryConsume(1))
            return userQueryService.getUserReservations(requestAnalyzer.getTokenFromRequest());
        else throw new RateLimitException("Too many get user reservations attempts");
    }

}
