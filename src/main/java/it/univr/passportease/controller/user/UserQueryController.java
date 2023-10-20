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

/**
 * Controller for user queries. It handles the following GraphQL queries:
 * <ul>
 *     <li>{@link UserQueryController#getRequestTypesByUser()}</li>
 *     <li>{@link UserQueryController#getReportDetailsByAvailabilityID(String)}</li>
 *     <li>{@link UserQueryController#getUserNotifications()}</li>
 *     <li>{@link UserQueryController#getUserDetails()}</li>
 *     <li>{@link UserQueryController#getUserReservations()}</li>
 * </ul>
 *
 * @see UserQueryService
 */
@Controller
@AllArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;

    BucketLimiter bucketLimiter;
    private RequestAnalyzer requestAnalyzer;

    /**
     * @return the list of request types associated to the user
     * @throws RateLimitException                         if the user has exceeded the rate limit
     * @throws RequestTypeNotFoundException               if the user has no request types associated
     * @throws AuthenticationCredentialsNotFoundException if the user has not included the token in the request
     */
    @QueryMapping
    public List<RequestType> getRequestTypesByUser() throws RateLimitException, RequestTypeNotFoundException, AuthenticationCredentialsNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_REQUEST_TYPES_BY_USER);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many get request types attempts");

        return userQueryService.getRequestTypesByUser(requestAnalyzer.getTokenFromRequest());
    }

    /**
     * @param availabilityId the availability id for which the report details are requested
     * @return see {@link ReportDetails}, containing all the information to generate the report for the given availability
     * @throws SecurityException              if the user is not authorized to access the report details requested,
     *                                        or if the user has not included the token in the request
     * @throws InvalidAvailabilityIDException if the availability id is not valid
     */
    @QueryMapping
    public ReportDetails getReportDetailsByAvailabilityID(@Argument("availabilityID") String availabilityId)
            throws SecurityException, InvalidAvailabilityIDException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_REPORT_DETAILS_BY_AVAILABILITY_ID);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many get notifications attempts");

        return userQueryService.getReportDetailsByAvailabilityID(availabilityId, requestAnalyzer.getTokenFromRequest());
    }

    /**
     * @return the list of notifications associated to the user
     * @throws AuthenticationCredentialsNotFoundException if the user has not included the token in the request
     * @throws RateLimitException                         if the user has exceeded the rate limit
     */
    @QueryMapping
    public List<Notification> getUserNotifications() throws AuthenticationCredentialsNotFoundException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_USER_NOTIFICATIONS);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many get notifications attempts");

        return userQueryService.getUserNotifications(requestAnalyzer.getTokenFromRequest());
    }

    /**
     * @return the user details associated to the user, for generating the profile page
     * @throws AuthenticationCredentialsNotFoundException if the user has not included the token in the request
     * @throws RateLimitException                         if the user has exceeded the rate limit
     * @throws UserNotFoundException                      if the user is not found
     */
    @QueryMapping
    public User getUserDetails() throws AuthenticationCredentialsNotFoundException, RateLimitException, UserNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_USER_DETAILS);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many get user details attempts");

        return userQueryService.getUserDetails(requestAnalyzer.getTokenFromRequest());
    }

    /**
     * @return the list of reservations associated to the user
     * @throws AuthenticationCredentialsNotFoundException if the user has not included the token in the request
     * @throws RateLimitException                         if the user has exceeded the rate limit
     */
    @QueryMapping
    public List<Availability> getUserReservations() throws AuthenticationCredentialsNotFoundException, RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.GET_USER_RESERVATIONS);
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many get user reservations attempts");

        return userQueryService.getUserReservations(requestAnalyzer.getTokenFromRequest());
    }

}
