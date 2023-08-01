package it.univr.passportease.service;

import it.univr.passportease.entity.Office;
import it.univr.passportease.repository.OfficeRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserWorkerQueryServiceImpl implements UserWorkerQueryService{

    private final OfficeRepository officeRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public List<Office> getOffices() {
        return officeRepository.findAll();
    }
}
