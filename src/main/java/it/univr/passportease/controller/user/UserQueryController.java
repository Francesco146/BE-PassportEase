package it.univr.passportease.controller.user;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;

public class UserQueryController {
    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public void getRequestTypesByUser() {}

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public void getReportDetailsByAvailabilityId() {}

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public void getUserNotifications() {}

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public void getUserDetails() {}

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public void getUserReservations() {}
}
