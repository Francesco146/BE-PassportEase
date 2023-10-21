package it.univr.passportease.service.worker;

import it.univr.passportease.dto.input.WorkerInput;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.exception.notfound.OfficeNotFoundException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.exception.security.WrongPasswordException;

/**
 * Service for worker authentication.
 */
public interface WorkerAuthService {
    /**
     * Logs in the worker with the given fiscal code and password.
     *
     * @param fiscalCode worker's fiscal code
     * @param password   worker's password
     * @return {@link LoginOutput} object containing the worker's JWTs
     * @throws WrongPasswordException  if the password is wrong
     * @throws WorkerNotFoundException if the worker is not found
     */
    LoginOutput login(String fiscalCode, String password) throws WrongPasswordException, WorkerNotFoundException;

    /**
     * Registers the worker with the given input.
     *
     * @param workerInput worker's data
     * @return {@link LoginOutput} object containing the worker's JWTs
     * @throws OfficeNotFoundException if the office is not found
     * @deprecated
     */
    @Deprecated(since = "0.0.1")
    LoginOutput register(WorkerInput workerInput) throws OfficeNotFoundException;
}
