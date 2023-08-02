package it.univr.passportease.controller.worker;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;
import it.univr.passportease.service.worker.WorkerMutationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WorkerMutationController {
    private final WorkerMutationService workerMutationService;
    @Autowired
    private HttpServletRequest request;

    @MutationMapping
    public Request createRequest(@Argument("request") RequestInput requestInput) throws RuntimeException {
        return workerMutationService.createRequest(getTokenFromRequest(), requestInput);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void modifyRequest() {
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public void deleteRequest() {
    }

    private String getTokenFromRequest() throws RuntimeException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new RuntimeException("Invalid token");
        return authorizationHeader.substring(7);
    }
}
