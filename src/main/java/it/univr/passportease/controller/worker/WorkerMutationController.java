package it.univr.passportease.controller.worker;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.service.worker.WorkerMutationService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WorkerMutationController {
    private final WorkerMutationService workerMutationService;
    private RequestAnalyzer requestAnalyzer;

    @MutationMapping
    public Request createRequest(@Argument("request") RequestInput requestInput) throws RuntimeException {
        return workerMutationService.createRequest(requestAnalyzer.getTokenFromRequest(), requestInput);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void modifyRequest() {
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void deleteRequest() {
    }
}
