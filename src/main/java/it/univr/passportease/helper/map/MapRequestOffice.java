package it.univr.passportease.helper.map;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestOffice;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Map {@link Request} and {@link Office} to {@link RequestOffice
 */
@Component
public class MapRequestOffice {
    /**
     * @param request {@link Request} to map
     * @param office  {@link Office} to map
     * @return {@link RequestOffice} mapped
     */
    public RequestOffice mapRequestAndOfficeToRequestOffice(Request request, Office office) {
        RequestOffice requestOffice = new RequestOffice();
        requestOffice.setRequest(request);
        requestOffice.setOffice(office);
        requestOffice.setCreatedAt(new Date());
        requestOffice.setUpdatedAt(new Date());
        return requestOffice;
    }
}
