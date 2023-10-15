package it.univr.passportease.controller.user;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.invalid.InvalidWorkerActionException;
import it.univr.passportease.exception.notfound.*;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.helper.ratelimiter.RateLimiter;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.user.UserMutationService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserMutationController {

    private final UserMutationService userMutationService;
    private final JwtService jwtService;

    private BucketLimiter bucketLimiter;
    private RequestAnalyzer requestAnalyzer;

    @MutationMapping
    public Availability createReservation(@Argument("availabilityID") String availabilityID)
            throws RateLimitException, AvailabilityNotFoundException, UserNotFoundException {

        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.CREATE_RESERVATION);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many createReservation attempts");

        Object user = jwtService.getUserOrWorkerFromToken(requestAnalyzer.getTokenFromRequest());

        if (!(user instanceof User))
            throw new InvalidWorkerActionException("Workers cannot create reservations");

        return userMutationService.createReservation(UUID.fromString(availabilityID), (User) user);
    }

    @MutationMapping
    public void deleteReservation(@Argument("availabilityID") String availabilityID) throws RateLimitException, AvailabilityNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.DELETE_RESERVATION);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many deleteReservation attempts");

        userMutationService.deleteReservation(UUID.fromString(availabilityID));
    }

    @MutationMapping
    public Notification createNotification(@Argument("notification") NotificationInput notificationInput)
            throws UserNotFoundException, InvalidWorkerActionException, AuthenticationCredentialsNotFoundException, RateLimitException, InvalidRequestTypeException, OfficeNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.CREATE_NOTIFICATION);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many createNotification attempts");

        Object user = jwtService.getUserOrWorkerFromToken(requestAnalyzer.getTokenFromRequest());
        if (!(user instanceof User)) throw new InvalidWorkerActionException("Workers cannot create notifications");

        return userMutationService.createNotification(notificationInput, (User) user);
    }

    @MutationMapping
    public Notification modifyNotification(@Argument("notification") NotificationInput notificationInput, @Argument("notificationID") UUID notificationId)
            throws RateLimitException, NotificationNotFoundException, OfficeNotFoundException, RequestTypeNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.MODIFY_NOTIFICATION);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many modifyNotification attempts");

        return userMutationService.modifyNotification(notificationInput, notificationId);
    }

    @MutationMapping
    public void deleteNotification(@Argument("notificationID") UUID notificationId) throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.DELETE_NOTIFICATION);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many deleteNotification attempts");

        userMutationService.deleteNotification(notificationId);
    }
}
