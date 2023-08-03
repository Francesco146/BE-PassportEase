package it.univr.passportease.service.worker;

import it.univr.passportease.dto.input.WorkerInput;
import it.univr.passportease.dto.output.LoginOutput;

public interface WorkerAuthService {
    LoginOutput login(String fiscalCode, String password);

    LoginOutput register(WorkerInput workerInput);
}
