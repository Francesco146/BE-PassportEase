package it.univr.passportease.controller.user;

import io.github.bucket4j.Bucket;
import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.invalid.InvalidWorkerActionException;
import it.univr.passportease.exception.notfound.OfficeNotFoundException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.RateLimitException;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.helper.ratelimiter.BucketLimiter;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.user.UserMutationService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserMutationController {

    private final UserMutationService userMutationService;
    private final JwtService jwtService;
    private RequestAnalyzer requestAnalyzer;
    private BucketLimiter bucketLimiter;

    @MutationMapping
    public void createReservation() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @MutationMapping
    public void deleteReservation() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @MutationMapping
    public Notification createNotification(@Argument("notification") NotificationInput notificationInput)
            throws UserNotFoundException, InvalidWorkerActionException, AuthenticationCredentialsNotFoundException, RateLimitException, InvalidRequestTypeException, OfficeNotFoundException {
        Bucket bucket = bucketLimiter.resolveBucket(bucketLimiter.getMethodName());
        if (!bucket.tryConsume(1))
            throw new RateLimitException("Too many createNotification attempts");

        Object user = jwtService.getUserOrWorkerFromToken(requestAnalyzer.getTokenFromRequest());
        if (user instanceof Worker)
            throw new InvalidWorkerActionException("Workers cannot create notifications");
        return userMutationService.createNotification(notificationInput, (User) user);
    }

    @MutationMapping
    public void modifyNotification() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @MutationMapping
    public void deleteNotification() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
