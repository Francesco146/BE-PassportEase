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

    @Autowired
    private HttpServletRequest request;

    private final UserQueryService userQueryService;
    @QueryMapping
    public void getRequestTypesByUser() {}

    @QueryMapping
    public void getReportDetailsByAvailabilityId() {}

    @QueryMapping
    public List<Notification> getUserNotifications() {
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        return userQueryService.getUserNotifications(token);
    }

    @QueryMapping
    public User getUserDetails() {
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        return userQueryService.getUserDetails(token);
    }

    @QueryMapping
    public List<Availability> getUserReservations() {
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        return userQueryService.getUserReservations(token);
    }
}
