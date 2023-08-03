package it.univr.passportease.controller.worker;

import it.univr.passportease.dto.input.WorkerInput;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.service.worker.WorkerAuthService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WorkerAuthController {
    private final WorkerAuthService workerAuthService;

    @MutationMapping
    public LoginOutput loginWorker(@Argument("username") String username, @Argument("password") String password) {
        return workerAuthService.login(username, password);
    }

    @MutationMapping
    public LoginOutput registerWorker(@Argument("workerInput") WorkerInput workerInput) {
        return workerAuthService.register(workerInput);
    }
}
