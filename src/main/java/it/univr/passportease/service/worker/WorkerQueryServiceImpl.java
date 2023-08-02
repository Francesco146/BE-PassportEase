package it.univr.passportease.service.worker;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.repository.RequestTypeRepository;
import it.univr.passportease.repository.ReservationRepository;
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
    //TODO
    //@PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public List<RequestType> getAllRequestTypes() {
        return requestTypeRepository.findAll();
    }

    @Override
    //TODO
    //@PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public Request getRequestByAvailabilityID(String id) {
        Optional<Availability> _availability = reservationRepository.findById(UUID.fromString(id));
        if (_availability.isEmpty())
           throw new RuntimeException("Invalid Availability ID");
        return _availability.get().getRequest();
    }
}
