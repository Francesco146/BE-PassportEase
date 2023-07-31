package it.univr.passportease.controller.worker;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WorkerQueryController {


    @QueryMapping
    public void getRequestsByAvailabilityId() {}

    @QueryMapping
    public void getAllRequestTypes() {}
}
