package it.univr.passportease.controller.user;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.user.UserMutationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserMutationController {

    private final UserMutationService userMutationService;
    private final JwtService jwtService;
    @Autowired
    private HttpServletRequest request;

    @MutationMapping
    public void createReservation() {
    }

    @MutationMapping
    public void deleteReservation() {
    }

    @MutationMapping
    public Notification createNotification(@Argument("notification") NotificationInput notificationInput) {
        Object user = jwtService.getUserOrWorkerFromToken(getTokenFromRequest());
        if (user instanceof Worker || user == null)
            throw new RuntimeException("Workers cannot create notifications");
        return userMutationService.createNotification(notificationInput, (User) user);
    }

    @MutationMapping
    public void modifyNotification() {
    }

    @MutationMapping
    public void deleteNotification() {
    }

    private String getTokenFromRequest() throws RuntimeException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new RuntimeException("Invalid token");
        return authorizationHeader.substring(7);
    }
}
