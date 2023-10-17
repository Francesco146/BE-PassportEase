package it.univr.passportease.service.user;

import it.univr.passportease.dto.output.ReportDetails;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.User;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.helper.JWT;

import java.util.List;

public interface UserQueryService {
    User getUserDetails(JWT token) throws UserNotFoundException;

    List<Notification> getUserNotifications(JWT token);

    List<Availability> getUserReservations(JWT token);

    ReportDetails getReportDetailsByAvailabilityID(String availabilityId, JWT token)
            throws SecurityException, InvalidAvailabilityIDException;

    List<RequestType> getRequestTypesByUser(JWT token) throws InvalidRequestTypeException;
}
