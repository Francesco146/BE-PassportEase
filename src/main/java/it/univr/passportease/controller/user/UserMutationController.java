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

/**
 * Controller for all the user mutations. It handles the following GraphQL mutations:
 * <ul>
 *     <li>{@link UserMutationController#createReservation(String)}</li>
 *     <li>{@link UserMutationController#deleteReservation(String)}</li>
 *     <li>{@link UserMutationController#createNotification(NotificationInput)}</li>
 *     <li>{@link UserMutationController#modifyNotification(NotificationInput, UUID)}</li>
 *     <li>{@link UserMutationController#deleteNotification(UUID)}</li>
 * </ul>
 *
 * @see UserMutationService
 */
@Controller
@AllArgsConstructor
public class UserMutationController {

    private final UserMutationService userMutationService;
    private final JwtService jwtService;

    private BucketLimiter bucketLimiter;
    private RequestAnalyzer requestAnalyzer;

    /**
     * This mutation creates a reservation from an availability. It returns the created reservation.
     *
     * @param availabilityID the availability ID to create the reservation from.
     * @return the created reservation
     * @throws RateLimitException            if the user has exceeded the rate limit
     * @throws AvailabilityNotFoundException if the availability is not found
     * @throws UserNotFoundException         if the user is not found
     */
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

    /**
     * This mutation deletes a reservation. It returns nothing.
     *
     * @param availabilityID the availability ID to delete.
     * @throws RateLimitException            if the user has exceeded the rate limit
     * @throws AvailabilityNotFoundException if the availability is not found
     */
    @MutationMapping
    public void deleteReservation(@Argument("availabilityID") String availabilityID) throws RateLimitException, AvailabilityNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.DELETE_RESERVATION);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many deleteReservation attempts");

        userMutationService.deleteReservation(UUID.fromString(availabilityID));
    }

    /**
     * @param notificationInput see {@link NotificationInput}, the notification to create
     * @return the created notification
     * @throws UserNotFoundException                      if the user is not found
     * @throws InvalidWorkerActionException               if the user is a worker
     * @throws AuthenticationCredentialsNotFoundException if the token is not in the request
     * @throws RateLimitException                         if the user has exceeded the rate limit
     * @throws InvalidRequestTypeException                if the request type is invalid
     * @throws OfficeNotFoundException                    if the office is not found
     */
    @MutationMapping
    public Notification createNotification(@Argument("notification") NotificationInput notificationInput)
            throws UserNotFoundException, InvalidWorkerActionException, AuthenticationCredentialsNotFoundException, RateLimitException, InvalidRequestTypeException, OfficeNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.CREATE_NOTIFICATION);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many createNotification attempts");

        Object user = jwtService.getUserOrWorkerFromToken(requestAnalyzer.getTokenFromRequest());
        if (!(user instanceof User)) throw new InvalidWorkerActionException("Workers cannot create notifications");

        return userMutationService.createNotification(notificationInput, (User) user);
    }

    /**
     * @param notificationInput see {@link NotificationInput}, the notification to modify
     * @param notificationId    the notification ID to modify
     * @return the modified notification
     * @throws RateLimitException            if the user has exceeded the rate limit
     * @throws NotificationNotFoundException if the notification is not found
     * @throws OfficeNotFoundException       if the office is not found
     * @throws RequestTypeNotFoundException  if the request type is not found
     */
    @MutationMapping
    public Notification modifyNotification(@Argument("notification") NotificationInput notificationInput, @Argument("notificationID") UUID notificationId)
            throws RateLimitException, NotificationNotFoundException, OfficeNotFoundException, RequestTypeNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.MODIFY_NOTIFICATION);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many modifyNotification attempts");

        return userMutationService.modifyNotification(notificationInput, notificationId);
    }

    /**
     * @param notificationId the notification ID to delete
     * @throws RateLimitException if the user has exceeded the rate limit
     */
    @MutationMapping
    public void deleteNotification(@Argument("notificationID") UUID notificationId) throws RateLimitException {
        Bucket bucket = bucketLimiter.resolveBucket(RateLimiter.DELETE_NOTIFICATION);
        if (!bucket.tryConsume(1)) throw new RateLimitException("Too many deleteNotification attempts");

        userMutationService.deleteNotification(notificationId);
    }
}
