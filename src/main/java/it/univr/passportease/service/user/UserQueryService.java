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

/**
 * Service for querying user data.
 */
public interface UserQueryService {
    /**
     * Get the user details from the JWT token.
     *
     * @param token JWT Access token
     * @return User details from the token
     * @throws UserNotFoundException if the user is not found
     */
    User getUserDetails(JWT token) throws UserNotFoundException;

    /**
     * Get the list of notifications of the user.
     *
     * @param token JWT Access token
     * @return List of notifications for the user
     */
    List<Notification> getUserNotifications(JWT token);

    /**
     * Get the list of reservations of the user.
     *
     * @param token JWT Access token
     * @return List of reservations for the user
     */
    List<Availability> getUserReservations(JWT token);

    /**
     * Get the list of information for the user to generate a report.
     *
     * @param availabilityId Availability ID
     * @param token          JWT Access token
     * @return Report details for the availability
     * @throws SecurityException              if the user is not the owner of the availability
     * @throws InvalidAvailabilityIDException if the availability ID is invalid
     */
    ReportDetails getReportDetailsByAvailabilityID(String availabilityId, JWT token)
            throws SecurityException, InvalidAvailabilityIDException;

    /**
     * Get the list of request types for the user.
     *
     * @param token JWT Access token
     * @return List of request types for the user
     * @throws InvalidRequestTypeException if the request type is invalid
     */
    List<RequestType> getRequestTypesByUser(JWT token) throws InvalidRequestTypeException;
}
