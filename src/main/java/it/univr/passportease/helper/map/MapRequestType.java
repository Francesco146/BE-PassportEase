package it.univr.passportease.helper.map;

import it.univr.passportease.dto.RequestTypeInput;
import it.univr.passportease.entity.RequestType;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MapRequestType {
    public RequestType mapInputToRequestType(RequestTypeInput requestTypeInput) {
        RequestType requestType = new RequestType();
        requestType.setName(requestTypeInput.getName());
        requestType.setCreatedAt(new Date());
        requestType.setUpdatedAt(new Date());
        return requestType;
    }
}
