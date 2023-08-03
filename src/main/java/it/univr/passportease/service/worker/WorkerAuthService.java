package it.univr.passportease.service.worker;

import it.univr.passportease.dto.input.WorkerInput;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.exception.notfound.OfficeNotFoundException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.exception.security.WrongPasswordException;

public interface WorkerAuthService {
    LoginOutput login(String fiscalCode, String password) throws WrongPasswordException, WorkerNotFoundException;

    LoginOutput register(WorkerInput workerInput) throws OfficeNotFoundException;
}
