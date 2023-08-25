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
    public List<Availability> getAvailabilities() {
        return availabilityRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public List<Availability> getAvailabilities(AvailabilityFilters availabilityFilters) throws InvalidDataFromRequestException {

        Date startDate = availabilityFilters.getStartDate();
        Date endDate = availabilityFilters.getEndDate();
        LocalTime startTime = availabilityFilters.getStartTime();
        LocalTime endTime = availabilityFilters.getEndTime();

        if (startDate != null && endDate != null && startDate.after(endDate))
            throw new InvalidDataFromRequestException("Start date must be before end date");

        if (startTime != null && endTime != null && startTime.isAfter(endTime))
            throw new InvalidDataFromRequestException("Start time must be before end time");

        for (String officeName : availabilityFilters.getOfficesName())
            if (!officeRepository.existsByName(officeName))
                throw new InvalidDataFromRequestException("Office " + officeName + " doesn't exist");

        for (String requestType : availabilityFilters.getRequestTypes())
            if (!requestTypeRepository.existsByName(requestType))
                throw new InvalidDataFromRequestException("Request type " + requestType + " doesn't exist");

        return availabilityRepository.findAll(Specification.where(availabilityFilters));
    }
}
