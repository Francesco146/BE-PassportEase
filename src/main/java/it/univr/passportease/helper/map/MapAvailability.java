package it.univr.passportease.helper.map;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import org.springframework.stereotype.Component;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.enums.Status;

@Component
public class MapAvailability {
    public Availability mapRequestToAvailability(Request request, Office office, LocalDate date, LocalTime time) {
        Availability availability = new Availability();
        availability.setStatus(Status.FREE);
        // cast LocalDate to Date
        Date _date = Date.from(date.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
        availability.setDate(_date);
        availability.setTime(time);
        availability.setRequest(request);
        availability.setOffice(office);
        availability.setCreatedAt(new Date());
        availability.setUpdatedAt(new Date());
        return availability;
    }
}
