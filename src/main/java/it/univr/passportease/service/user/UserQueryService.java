package it.univr.passportease.service.user;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.dto.output.ReportDetails;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.exception.notfound.UserNotFoundException;

import java.util.List;

public interface UserQueryService {
    User getUserDetails(String token) throws UserNotFoundException;

    List<Notification> getUserNotifications(String token);

    List<Availability> getUserReservations(String token);

    ReportDetails getReportDetailsByAvailabilityID(String availabilityId, String token)
            throws SecurityException, InvalidAvailabilityIDException;

}
