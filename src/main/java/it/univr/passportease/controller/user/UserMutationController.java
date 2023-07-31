package it.univr.passportease.controller.user;

import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;

public class UserMutationController {
    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public void createReservation(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public void deleteReservation(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public void createNotification(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public void modifyNotification(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public void deleteNotification(){}
}
