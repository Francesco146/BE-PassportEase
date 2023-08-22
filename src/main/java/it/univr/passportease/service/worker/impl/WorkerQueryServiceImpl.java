package it.univr.passportease.service.worker.impl;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.repository.RequestTypeRepository;
import it.univr.passportease.repository.ReservationRepository;
import it.univr.passportease.service.worker.WorkerQueryService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WorkerQueryServiceImpl implements WorkerQueryService {

    private final RequestTypeRepository requestTypeRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public List<RequestType> getAllRequestTypes() {
        return requestTypeRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public Request getRequestByAvailabilityID(String id) throws InvalidAvailabilityIDException {
        Optional<Availability> availability = reservationRepository.findById(UUID.fromString(id));
        if (availability.isEmpty())
            throw new InvalidAvailabilityIDException("Invalid Availability ID");
        return availability.get().getRequest();
    }
}
