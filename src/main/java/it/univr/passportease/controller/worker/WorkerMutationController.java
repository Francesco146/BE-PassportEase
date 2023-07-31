package it.univr.passportease.controller.worker;

import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;

public class WorkerMutationController {
    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void createRequest(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void modifyRequest(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void deleteRequest(){}
}
