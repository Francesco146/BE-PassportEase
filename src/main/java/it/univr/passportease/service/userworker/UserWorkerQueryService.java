package it.univr.passportease.service.userworker;

import it.univr.passportease.dto.input.AvailabilityFilters;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Office;
import it.univr.passportease.exception.invalid.InvalidDataFromRequestException;
import it.univr.passportease.helper.JWT;

import java.util.List;

/**
 * Service for querying data for the user and worker.
 */
public interface UserWorkerQueryService {

    /**
     * Get the list of offices.
     *
     * @return the list of offices.
     */
    List<Office> getOffices();

    /**
     * Get the list of availabilities.
     *
     * @param availabilityFilters the filters for the query.
     * @param page                the page number.
     * @param size                the page size (number of elements) per page.
     * @return the list of availabilities.
     * @throws InvalidDataFromRequestException if the data from the request are invalid.
     */
    List<Availability> getAvailabilities(JWT jwt, AvailabilityFilters availabilityFilters, Integer page, Integer size) throws InvalidDataFromRequestException;
}
