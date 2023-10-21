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
}
