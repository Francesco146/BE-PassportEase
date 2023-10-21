package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.*;

import java.util.UUID;

/**
 * Service that handles the mutation of the user entity.
 */
public interface UserMutationService {
    /**
     * Creates a new notification.
     *
     * @param notificationInput the notification to be created
     * @param user              the user that creates the notification
     * @return the created notification
     * @throws OfficeNotFoundException     if the office specified in the notification does not exist
     * @throws InvalidRequestTypeException if the request type specified in the notification is invalid
     */
    Notification createNotification(NotificationInput notificationInput, User user) throws OfficeNotFoundException, InvalidRequestTypeException;

    /**
     * Deletes a notification.
     *
     * @param notificationId the id of the notification to be deleted
     */
    void deleteNotification(UUID notificationId);

    /**
     * Modifies a notification.
     *
     * @param notificationInput the new notification data
     * @param notificationId    the id of the notification to be modified
     * @return the modified notification
     * @throws NotificationNotFoundException if the notification to be modified does not exist
     * @throws OfficeNotFoundException       if the office specified in the notification does not exist
     * @throws RequestTypeNotFoundException  if the request type specified in the notification does not exist
     */
    Notification modifyNotification(NotificationInput notificationInput, UUID notificationId) throws NotificationNotFoundException, OfficeNotFoundException, RequestTypeNotFoundException;

    /**
     * Creates a new availability.
     *
     * @param availabilityId the id of the availability to be created
     * @param user           the user that creates the availability
     * @return the created availability
     * @throws AvailabilityNotFoundException if the availability to be created does not exist
     * @throws UserNotFoundException         if the user that creates the availability does not exist
     */
    Availability createReservation(UUID availabilityId, User user) throws AvailabilityNotFoundException, UserNotFoundException;

    /**
     * Deletes an availability.
     *
     * @param availabilityId the id of the availability to be deleted
     * @throws AvailabilityNotFoundException if the availability to be deleted does not exist
     */
    void deleteReservation(UUID availabilityId) throws AvailabilityNotFoundException;
}
