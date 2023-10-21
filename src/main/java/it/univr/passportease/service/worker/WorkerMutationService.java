package it.univr.passportease.service.worker;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.RequestNotFoundException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.helper.JWT;

/**
 * Service for worker mutation operations.
 */
public interface WorkerMutationService {
    /**
     * Creates a request.
     *
     * @param token        JWT access token of the worker
     * @param requestInput request input
     * @return the created request
     * @throws WorkerNotFoundException     if the worker is not found
     * @throws InvalidRequestTypeException if the request type is invalid
     * @throws OfficeOverloadedException   if the office hasn't enough workers
     */
    Request createRequest(JWT token, RequestInput requestInput)
            throws WorkerNotFoundException, InvalidRequestTypeException, OfficeOverloadedException;

    /**
     * Modifies a request.
     *
     * @param token        JWT access token of the worker
     * @param requestID    ID of the request to modify
     * @param requestInput request input
     * @return the modified request
     * @throws WorkerNotFoundException   if the worker is not found
     * @throws RequestNotFoundException  if the request is not found
     * @throws OfficeOverloadedException if the office hasn't enough workers
     */
    Request modifyRequest(JWT token, String requestID, RequestInput requestInput)
            throws WorkerNotFoundException, RequestNotFoundException, OfficeOverloadedException;

    /**
     * Deletes a request.
     *
     * @param token     JWT access token of the worker
     * @param requestID ID of the request to delete
     * @throws WorkerNotFoundException  if the worker is not found
     * @throws RequestNotFoundException if the request is not found
     */
    void deleteRequest(JWT token, String requestID)
            throws WorkerNotFoundException, RequestNotFoundException;
}
