package it.univr.passportease.helper.map;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * Map a {@link Request} to an {@link Availability} object.
 */
@Component
public class MapAvailability {
    /**
     * Map a {@link Request} to an {@link Availability} object.
     *
     * @param request   The request to map.
     * @param office    The office to map.
     * @param localDate The date to map.
     * @param time      The time to map.
     * @return The mapped {@link Availability} object.
     */
    public Availability mapRequestToAvailability(Request request, Office office, LocalDate localDate, LocalTime time) {
        Date date = java.sql.Date.valueOf(localDate);
        Availability availability = new Availability();
        availability.setStatus(Status.FREE);
        availability.setDate(date);
        availability.setTime(time);
        availability.setRequest(request);
        availability.setOffice(office);
        availability.setCreatedAt(new Date());
        availability.setUpdatedAt(new Date());
        return availability;
    }
}
