package it.univr.passportease.helper.map;

import it.univr.passportease.entity.RequestType;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MapRequestType {
    public RequestType mapStringToRequestType(String name) {
        RequestType requestType = new RequestType();
        requestType.setName(name);
        requestType.setHasDependency(true);
        requestType.setCreatedAt(new Date());
        requestType.setUpdatedAt(new Date());
        return requestType;
    }
}
