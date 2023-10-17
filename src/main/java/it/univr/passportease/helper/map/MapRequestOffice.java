package it.univr.passportease.helper.map;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestOffice;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MapRequestOffice {
    public RequestOffice mapRequestAndOfficeToRequestOffice(Request request, Office office) {
        RequestOffice requestOffice = new RequestOffice();
        requestOffice.setRequest(request);
        requestOffice.setOffice(office);
        requestOffice.setCreatedAt(new Date());
        requestOffice.setUpdatedAt(new Date());
        return requestOffice;
    }
}
