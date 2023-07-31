package it.univr.passportease.service.requesttype;

import it.univr.passportease.dto.RequestTypeInput;
import it.univr.passportease.entity.RequestType;

import java.util.List;

public interface RequestTypeService {

    RequestType add(RequestTypeInput requestTypeInput);

    List<RequestType> getAll();
}
