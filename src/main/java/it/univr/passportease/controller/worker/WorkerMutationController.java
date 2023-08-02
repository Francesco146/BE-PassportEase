package it.univr.passportease.controller.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;
import it.univr.passportease.service.worker.WorkerMutationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WorkerMutationController {
    @Autowired
    private HttpServletRequest request;

    private final WorkerMutationService workerMutationService;

    @MutationMapping
    public Request createRequest(@Argument("request") RequestInput requestInput){
        String token = request.getHeader("Authorization").substring(7);
        return workerMutationService.createRequest(token, requestInput);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void modifyRequest(){}

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void deleteRequest(){}
}
