package it.univr.passportease.service.userworker.impl;

import it.univr.passportease.dto.input.AvailabilityFilters;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Office;
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

@Service
@AllArgsConstructor
public class UserWorkerQueryServiceImpl implements UserWorkerQueryService {

    private final OfficeRepository officeRepository;
    private final AvailabilityRepository availabilityRepository;
    private final RequestTypeRepository requestTypeRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public List<Office> getOffices() {
        return officeRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public List<Availability> getAvailabilities(AvailabilityFilters availabilityFilters, Integer page, Integer size) throws InvalidDataFromRequestException {

        boolean wantFiltered = availabilityFilters != null;
        boolean wantPaged = size != null && page != null;

        if (!wantFiltered)
            return wantPaged ?
                    availabilityRepository.findAll(PageRequest.of(page, size)).getContent() : availabilityRepository.findAll();

        filtersValidation(availabilityFilters);
        return wantPaged ?
                availabilityRepository.findAll(Specification.where(availabilityFilters), PageRequest.of(page, size)).getContent() : availabilityRepository.findAll(Specification.where(availabilityFilters));
    }

    private void filtersValidation(AvailabilityFilters availabilityFilters) throws InvalidDataFromRequestException {
        Date startDate = availabilityFilters.getStartDate();
        Date endDate = availabilityFilters.getEndDate();
        LocalTime startTime = availabilityFilters.getStartTime();
        LocalTime endTime = availabilityFilters.getEndTime();

        if (startDate != null && endDate != null && startDate.after(endDate))
            throw new InvalidDataFromRequestException("Start date must be before end date");

        if (startTime != null && endTime != null && startTime.isAfter(endTime))
            throw new InvalidDataFromRequestException("Start time must be before end time");

        if (availabilityFilters.getOfficesName() != null && !availabilityFilters.getOfficesName().isEmpty())
            validateOffices(availabilityFilters);

        if (availabilityFilters.getRequestTypes() != null && !availabilityFilters.getRequestTypes().isEmpty())
            validateRequestTypes(availabilityFilters);
    }

    private void validateRequestTypes(AvailabilityFilters availabilityFilters) {
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

    private void validateOffices(AvailabilityFilters availabilityFilters) {
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
