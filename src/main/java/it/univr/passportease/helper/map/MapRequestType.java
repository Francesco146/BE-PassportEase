package it.univr.passportease.helper.map;

import java.util.Date;

import org.springframework.stereotype.Component;

import it.univr.passportease.entity.RequestType;

@Component
public class MapRequestType {
    public RequestType mapStringToRequestType(String name) {
        RequestType requestType = new RequestType();
        requestType.setName(name);
        requestType.setCreatedAt(new Date());
        requestType.setUpdatedAt(new Date());
        return requestType;
    }
}
