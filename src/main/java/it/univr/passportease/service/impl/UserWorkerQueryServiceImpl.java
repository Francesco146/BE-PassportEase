package it.univr.passportease.service.impl;

import it.univr.passportease.entity.Office;
import it.univr.passportease.repository.AvailabilityRepository;
import it.univr.passportease.repository.OfficeRepository;
import it.univr.passportease.service.UserWorkerQueryService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserWorkerQueryServiceImpl implements UserWorkerQueryService {

    private final OfficeRepository officeRepository;
    private final AvailabilityRepository availabilityRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public List<Office> getOffices() {
        return officeRepository.findAll();
    }

    //@Override
    //@PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    //public List<Availability> getAvailabilites() {
    //    return availabilityRepository.findAll();
    //}

}
