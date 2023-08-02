package it.univr.passportease.controller.user;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.user.UserMutationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserMutationController {

    @Autowired
    private HttpServletRequest request;

    private final UserMutationService userMutationService;
    private final JwtService jwtService;

    @MutationMapping
    public void createReservation(){}

    @MutationMapping
    public void deleteReservation(){}

    @MutationMapping
    public Notification createNotification(@Argument("notification") NotificationInput notificationInput){
        String token = request.getHeader("Authorization").substring(7);
        User user = jwtService.getUserFromToken(token);
        return userMutationService.createNotification(notificationInput, user);
    }

    @MutationMapping
    public void modifyNotification(){}

    @MutationMapping
    public void deleteNotification(){}
}
