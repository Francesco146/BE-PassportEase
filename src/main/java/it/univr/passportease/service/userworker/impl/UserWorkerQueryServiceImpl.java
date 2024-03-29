package it.univr.passportease.service.userworker.impl;

import it.univr.passportease.dto.input.AvailabilityFilters;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.exception.invalid.InvalidDataFromRequestException;
import it.univr.passportease.repository.AvailabilityRepository;
import it.univr.passportease.repository.OfficeRepository;
import it.univr.passportease.repository.RequestTypeRepository;
import it.univr.passportease.service.userworker.UserWorkerQueryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * Implementation of {@link UserWorkerQueryService}
 */
@Service
@AllArgsConstructor
public class UserWorkerQueryServiceImpl implements UserWorkerQueryService {

    /**
     * Repository for {@link Office} entity
     */
    private final OfficeRepository officeRepository;
    /**
     * Repository for {@link Availability} entity
     */
    private final AvailabilityRepository availabilityRepository;
    /**
     * Repository for {@link RequestType} entity
     */
    private final RequestTypeRepository requestTypeRepository;

    /**
     * Get all the offices
     *
     * @return all the offices
     */
    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public List<Office> getOffices() {
        return officeRepository.findAll();
    }

    /**
     * Get all the availabilities that match the filters
     *
     * @param availabilityFilters filters to apply to the query, can be null
     * @param page                page number, can be null
     * @param size                page size, can be null
     * @return all the availabilities that match the filters
     * @throws InvalidDataFromRequestException if the filters are not valid, see {@link #filtersValidation(AvailabilityFilters)}
     */
    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public List<Availability> getAvailabilities(AvailabilityFilters availabilityFilters, Integer page, Integer size) throws InvalidDataFromRequestException {

        boolean wantFiltered = availabilityFilters != null;
        boolean wantPaged = size != null && page != null;

        if (!wantFiltered) {
            AvailabilityFilters defaultFilters = new AvailabilityFilters(null, null, null, null, null, null);
            return wantPaged ?
                    availabilityRepository
                            .findAll(Specification.where(defaultFilters), PageRequest.of(page, size))
                            .getContent()
                    :
                    availabilityRepository
                            .findAll(Specification.where(defaultFilters));
        }

        filtersValidation(availabilityFilters);
        return wantPaged ?
                availabilityRepository.findAll(Specification.where(availabilityFilters), PageRequest.of(page, size)).getContent() :
                availabilityRepository.findAll(Specification.where(availabilityFilters));
    }

    /**
     * The filters are not valid if:
     * <ul>
     *      <li>start date is after end date</li>
     *      <li>start time is after end time</li>
     *      <li>one of the offices doesn't exist</li>
     *      <li>one of the request types doesn't exist</li>
     * </ul>
     *
     * @param availabilityFilters filters to apply to the query
     * @throws InvalidDataFromRequestException if the filters are not valid, i.e.:
     */
    private void filtersValidation(AvailabilityFilters availabilityFilters) throws InvalidDataFromRequestException {
        Date startDate = availabilityFilters.getStartDate();
        Date endDate = availabilityFilters.getEndDate();
        LocalTime startTime = availabilityFilters.getStartTime();
        LocalTime endTime = availabilityFilters.getEndTime();

        if (areNotDatesValid(startDate, endDate) || areNotTimesValid(startTime, endTime))
            throw new InvalidDataFromRequestException("Invalid dates or times ranges");

        validateOffices(availabilityFilters);

        validateRequestTypes(availabilityFilters);
    }

    /**
     * @param startDate start date
     * @param endDate   end date
     * @return true if the start date is after the end date, false otherwise
     */
    private boolean areNotDatesValid(Date startDate, Date endDate) {
        return startDate != null && endDate != null && startDate.after(endDate);
    }

    /**
     * @param startTime start time
     * @param endTime   end time
     * @return true if the start time is after the end time, false otherwise
     */
    private boolean areNotTimesValid(LocalTime startTime, LocalTime endTime) {
        return startTime != null && endTime != null && startTime.isAfter(endTime);
    }

    /**
     * For each request type, check if it exists
     *
     * @param availabilityFilters filters to apply to the query
     * @throws InvalidDataFromRequestException if one of the request types doesn't exist
     */
    private void validateRequestTypes(AvailabilityFilters availabilityFilters)
            throws InvalidDataFromRequestException {
        if (availabilityFilters.getRequestTypes() != null && !availabilityFilters.getRequestTypes().isEmpty())
            availabilityFilters
                    .getRequestTypes()
                    .stream()
                    .filter(requestType ->
                            requestTypeRepository.findByName(requestType).isEmpty())
                    .forEach(requestType -> {
                                throw new InvalidDataFromRequestException("Request type " + requestType + " doesn't exist");
                            }
                    );
    }

    /**
     * For each office, check if it exists
     *
     * @param availabilityFilters filters to apply to the query
     * @throws InvalidDataFromRequestException if one of the offices doesn't exist
     */
    private void validateOffices(AvailabilityFilters availabilityFilters) throws InvalidDataFromRequestException {
        if (availabilityFilters.getOfficesName() != null && !availabilityFilters.getOfficesName().isEmpty())
            availabilityFilters
                    .getOfficesName()
                    .stream()
                    .filter(officeName ->
                            officeRepository.findByName(officeName).isEmpty())
                    .forEach(officeName -> {
                                throw new InvalidDataFromRequestException("Office " + officeName + " doesn't exist");
                            }
                    );
    }
}
