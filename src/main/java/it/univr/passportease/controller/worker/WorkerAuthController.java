package it.univr.passportease.controller.worker;

import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;

public class WorkerAuthController {

    @MutationMapping
    public void loginWorker() {}

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void logoutWorker(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void refreshAccessTokenWorker(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void changePasswordWorker(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void changeEmailWorker(){}
}
