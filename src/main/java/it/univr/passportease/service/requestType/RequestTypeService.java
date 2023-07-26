package it.univr.passportease.service.requestType;

import it.univr.passportease.entity.RequestType;
import it.univr.passportease.dto.RequestTypeInput;

import java.util.List;

public interface RequestTypeService {
    
    RequestType add(RequestTypeInput requestTypeInput);

    List<RequestType> getAll();
}
