package it.univr.passportease.service.worker;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.helper.JWT;

public interface WorkerMutationService {
    Request createRequest(JWT token, RequestInput requestInput) throws WorkerNotFoundException, InvalidRequestTypeException, OfficeOverloadedException;

    Request modifyRequest(JWT token, String requestID, RequestInput requestInput);

    void deleteRequest(JWT token, String requestID);
}
