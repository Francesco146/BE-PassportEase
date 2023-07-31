package it.univr.passportease.controller;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;

public class UserWorkerQueryController {
    @QueryMapping
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER')")
    public void getAvailabilities() {}

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER')")
    public void getOffices() {}
}
