package it.univr.passportease.controller.user;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.output.ReportDetails;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.User;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.exception.notfound.RequestTypeNotFoundException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.helper.ratelimiter.RateLimiter;
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
    public List<RequestType> getRequestTypesByUser() throws RateLimitException, RequestTypeNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_REQUEST_TYPES_BY_USER);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many get request types attempts");

        return userQueryService.getRequestTypesByUser(requestAnalyzer.getTokenFromRequest());

    }

    @QueryMapping
    public ReportDetails getReportDetailsByAvailabilityID(@Argument("availabilityID") String availabilityId)
            throws SecurityException, InvalidAvailabilityIDException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_REPORT_DETAILS_BY_AVAILABILITY_ID);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many get notifications attempts");

        return userQueryService.getReportDetailsByAvailabilityID(availabilityId, requestAnalyzer.getTokenFromRequest());
    }

    @QueryMapping
    public List<Notification> getUserNotifications() throws AuthenticationCredentialsNotFoundException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_USER_NOTIFICATIONS);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many get notifications attempts");

        return userQueryService.getUserNotifications(requestAnalyzer.getTokenFromRequest());
    }

    @QueryMapping
    public User getUserDetails() throws AuthenticationCredentialsNotFoundException, RateLimitException, UserNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_USER_DETAILS);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many get user details attempts");

        return userQueryService.getUserDetails(requestAnalyzer.getTokenFromRequest());
    }

    @QueryMapping
    public List<Availability> getUserReservations() throws AuthenticationCredentialsNotFoundException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_USER_RESERVATIONS);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many get user reservations attempts");

        return userQueryService.getUserReservations(requestAnalyzer.getTokenFromRequest());
    }

}
