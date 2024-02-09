package it.univr.passportease.service.worker.impl;

import it.univr.passportease.dto.input.RequestFilters;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.exception.invalid.InvalidDataFromRequestException;
import it.univr.passportease.repository.OfficeRepository;
import it.univr.passportease.repository.RequestRepository;
import it.univr.passportease.repository.RequestTypeRepository;
import it.univr.passportease.repository.ReservationRepository;
import it.univr.passportease.service.worker.WorkerQueryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link WorkerQueryService}.
 */
@Service
@AllArgsConstructor
public class WorkerQueryServiceImpl implements WorkerQueryService {

    /**
     * Repository of {@link RequestType} entities.
     */
    private final RequestTypeRepository requestTypeRepository;
    /**
     * Repository of {@link Availability} entities.
     */
    private final ReservationRepository reservationRepository;
    private final RequestRepository requestRepository;
    private final OfficeRepository officeRepository;

    /**
     * Get all the {@link RequestType}.
     *
     * @return a list of all the {@link RequestType}.
     */
    @Override
    @PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public List<RequestType> getAllRequestTypes() {
        return requestTypeRepository.findAll();
    }

    /**
     * Get the {@link Request} associated to the {@link Availability} with the given id.
     *
     * @param id the {@link Availability} id.
     * @return the {@link Request} associated to the {@link Availability} with the given id.
     * @throws InvalidAvailabilityIDException if the given id is not valid.
     */
    @Override
    @PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public Request getRequestByAvailabilityID(String id) throws InvalidAvailabilityIDException {
        Optional<Availability> availability = reservationRepository.findById(UUID.fromString(id));
        if (availability.isEmpty())
            throw new InvalidAvailabilityIDException("Invalid Availability ID");
        return availability.get().getRequest();
    }

    @Override
    @PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public List<Request> getRequests(RequestFilters filters, Integer page, Integer size) {
        boolean wantFiltered = filters != null;
        boolean wantPaged = size != null && page != null;

        if (!wantFiltered) {
            RequestFilters defaultFilters = new RequestFilters(null, null, null, null, null, null);
            return wantPaged ?
                    requestRepository
                            .findAll(Specification.where(defaultFilters), PageRequest.of(page, size))
                            .getContent()
                    :
                    requestRepository
                            .findAll(Specification.where(defaultFilters));
        }

        filtersValidation(filters);
        return wantPaged ?
                requestRepository.findAll(Specification.where(filters), PageRequest.of(page, size)).getContent() :
                requestRepository.findAll(Specification.where(filters));
    }

    private void filtersValidation(RequestFilters filters) throws InvalidDataFromRequestException {
        Date startDate = filters.getStartDate();
        Date endDate = filters.getEndDate();
        LocalTime startTime = filters.getStartTime();
        LocalTime endTime = filters.getEndTime();

        if (areNotDatesValid(startDate, endDate) || areNotTimesValid(startTime, endTime))
            throw new InvalidDataFromRequestException("Invalid dates or times ranges");

        validateOffices(filters);

        validateRequestTypes(filters);
    }

    private void validateRequestTypes(RequestFilters filters)
            throws InvalidDataFromRequestException {
        if (filters.getRequestTypes() != null && !filters.getRequestTypes().isEmpty())
            filters
                    .getRequestTypes()
                    .stream()
                    .filter(requestType ->
                            requestTypeRepository.findByName(requestType).isEmpty())
                    .forEach(requestType -> {
                                throw new InvalidDataFromRequestException("Request type " + requestType + " doesn't exist");
                            }
                    );
    }

    private void validateOffices(RequestFilters filters) throws InvalidDataFromRequestException {
        if (filters.getOfficesName() != null && !filters.getOfficesName().isEmpty())
            filters
                    .getOfficesName()
                    .stream()
                    .filter(officeName ->
                            officeRepository.findByName(officeName).isEmpty())
                    .forEach(officeName -> {
                                throw new InvalidDataFromRequestException("Office " + officeName + " doesn't exist");
                            }
                    );
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
}
