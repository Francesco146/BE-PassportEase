package it.univr.passportease.service.requesttype;

import it.univr.passportease.dto.RequestTypeInput;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.helper.map.MapRequestType;
import it.univr.passportease.repository.RequestTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RequestTypeServiceImpl implements RequestTypeService {
    private final RequestTypeRepository requestTypeRepository;
    private final MapRequestType mapRequestType;

    @Override
    public RequestType add(RequestTypeInput requestTypeInput) {
        RequestType requestType = mapRequestType.mapInputToRequestType(requestTypeInput);
        return requestTypeRepository.save(requestType);
    }

    @Override
    public List<RequestType> get() {
        return requestTypeRepository.get();
    }

}
