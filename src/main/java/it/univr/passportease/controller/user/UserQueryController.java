package it.univr.passportease.controller.user;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.service.user.UserQueryService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;
    private RequestAnalyzer requestAnalyzer;

    @QueryMapping
    public void getRequestTypesByUser() {
    }

    @QueryMapping
    public void getReportDetailsByAvailabilityId() {
    }

    @QueryMapping
    public List<Notification> getUserNotifications() {
        return userQueryService.getUserNotifications(requestAnalyzer.getTokenFromRequest());
    }

    @QueryMapping
    public User getUserDetails() throws RuntimeException {
        return userQueryService.getUserDetails(requestAnalyzer.getTokenFromRequest());
    }

    @QueryMapping
    public List<Availability> getUserReservations() throws RuntimeException {
        return userQueryService.getUserReservations(requestAnalyzer.getTokenFromRequest());
    }

}
