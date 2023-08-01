package it.univr.passportease.controller;

import it.univr.passportease.entity.Office;
import it.univr.passportease.service.UserWorkerQueryService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserWorkerQueryController {

    private final UserWorkerQueryService userWorkerQueryService;
    @QueryMapping
    public void getAvailabilities() {}

    @QueryMapping
    public List<Office> getOffices() {
        return userWorkerQueryService.getOffices();
    }
}
