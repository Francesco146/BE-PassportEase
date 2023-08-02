package it.univr.passportease.controller.worker;

import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.service.worker.WorkerQueryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;

import java.util.List;

@Controller
@AllArgsConstructor
public class WorkerQueryController {

    private final WorkerQueryService workerQueryService;


    @QueryMapping
    public Request getRequestsByAvailabilityId(@Argument("availabilityID") String id) {
        return workerQueryService.getRequestByAvailabilityID(id);
    }

    @QueryMapping
    public List<RequestType> getAllRequestTypes() {
        return workerQueryService.getAllRequestTypes();
    }
}
