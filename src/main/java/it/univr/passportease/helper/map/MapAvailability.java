package it.univr.passportease.helper.map;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Component
public class MapAvailability {
    public Availability mapRequestToAvailability(Request request, Office office, LocalDate localDate, LocalTime time) {
        Availability availability = new Availability();
        availability.setStatus(Status.FREE);
        // cast LocalDate to Date
        Date date = Date.from(localDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
        availability.setDate(date);
        availability.setTime(time);
        availability.setRequest(request);
        availability.setOffice(office);
        availability.setCreatedAt(new Date());
        availability.setUpdatedAt(new Date());
        return availability;
    }
}
