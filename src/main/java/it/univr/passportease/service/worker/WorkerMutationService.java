package it.univr.passportease.service.worker;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;

public interface WorkerMutationService {
    Request createRequest(String token, RequestInput requestInput) throws WorkerNotFoundException, InvalidRequestTypeException, OfficeOverloadedException;
}
