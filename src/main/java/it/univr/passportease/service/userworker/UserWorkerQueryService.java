package it.univr.passportease.service.userworker;

import it.univr.passportease.dto.input.AvailabilityFilters;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Office;
import it.univr.passportease.exception.invalid.InvalidDataFromRequestException;

import java.util.List;

public interface UserWorkerQueryService {

    List<Office> getOffices();

    List<Availability> getAvailabilities(AvailabilityFilters availabilityFilters, Integer page, Integer size) throws InvalidDataFromRequestException;
}
