package it.univr.passportease.controller.worker;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import it.univr.passportease.service.requesttype.RequestTypeService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WorkerQueryController {
    private final RequestTypeService requestTypeService;

    @QueryMapping
    public void getRequestsByAvailabilityId() {}

    @QueryMapping
    public void getAllRequestTypes() {
        requestTypeService.getAll();
    }
}
