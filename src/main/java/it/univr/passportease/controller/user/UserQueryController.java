package it.univr.passportease.controller.user;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.service.user.UserQueryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;
    @Autowired
    private HttpServletRequest request;

    @QueryMapping
    public void getRequestTypesByUser() {
    }

    @QueryMapping
    public void getReportDetailsByAvailabilityId() {
    }

    @QueryMapping
    public List<Notification> getUserNotifications() {
        return userQueryService.getUserNotifications(getTokenFromRequest());
    }

    @QueryMapping
    public User getUserDetails() throws RuntimeException {
        return userQueryService.getUserDetails(getTokenFromRequest());
    }

    @QueryMapping
    public List<Availability> getUserReservations() throws RuntimeException {
        return userQueryService.getUserReservations(getTokenFromRequest());
    }

    private String getTokenFromRequest() throws RuntimeException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new RuntimeException("Invalid token");
        return authorizationHeader.substring(7);
    }
}
