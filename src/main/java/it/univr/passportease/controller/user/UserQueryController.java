package it.univr.passportease.controller.user;

import it.univr.passportease.dto.output.UserOutput;
import it.univr.passportease.service.user.UserQueryService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@AllArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;
    @QueryMapping
    public void getRequestTypesByUser() {}

    @QueryMapping
    public void getReportDetailsByAvailabilityId() {}

    @QueryMapping
    public void getUserNotifications() {}

    @QueryMapping
    public UserOutput getUserDetails(@RequestHeader("Authorization") String token) {
        System.out.println(token);
        return userQueryService.getUserDetails(token);
    }

    @QueryMapping
    public void getUserReservations() {}
}
