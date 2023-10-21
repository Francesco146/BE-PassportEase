package it.univr.passportease.helper.map;

import it.univr.passportease.entity.RequestType;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Map {@link String} to {@link RequestType}
 */
@Component
public class MapRequestType {
    /**
     * Map {@link String} to {@link RequestType}
     *
     * @param name {@link String} name of the {@link RequestType} to map
     * @return {@link RequestType} mapped
     */
    public RequestType mapStringToRequestType(String name) {
        RequestType requestType = new RequestType();
        requestType.setName(name);
        requestType.setHasDependency(true);
        requestType.setCreatedAt(new Date());
        requestType.setUpdatedAt(new Date());
        return requestType;
    }
}
