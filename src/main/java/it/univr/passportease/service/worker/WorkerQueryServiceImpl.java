package it.univr.passportease.service.worker;

import it.univr.passportease.entity.RequestType;
import it.univr.passportease.repository.RequestTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WorkerQueryServiceImpl implements WorkerQueryService {

    private final RequestTypeRepository requestTypeRepository;

    @Override
    //TODO
    //@PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public List<RequestType> getAllRequestTypes() {
        return requestTypeRepository.findAll();
    }
}
