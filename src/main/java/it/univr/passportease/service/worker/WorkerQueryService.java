package it.univr.passportease.service.worker;

import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;

import java.util.List;

/**
 * Service for querying the database for information about requests. Used only by workers.
 */
public interface WorkerQueryService {

    /**
     * Get all the requests in the database.
     *
     * @return a list of all the request types in the database
     */
    List<RequestType> getAllRequestTypes();

    /**
     * Get all the requests with the given availability id.
     *
     * @param id the id of the request to retrieve
     * @return the request with the given id
     * @throws InvalidAvailabilityIDException if the given id is not a valid availability id
     */
    Request getRequestByAvailabilityID(String id) throws InvalidAvailabilityIDException;

}
