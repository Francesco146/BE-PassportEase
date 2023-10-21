package it.univr.passportease.service.user.impl;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.dto.input.NotificationInputDB;
import it.univr.passportease.entity.*;
import it.univr.passportease.entity.enums.Status;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.*;
import it.univr.passportease.helper.map.MapNotification;
import it.univr.passportease.repository.*;
import it.univr.passportease.service.user.UserMutationService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * This class implements the {@link UserMutationService} interface.
 * It contains all the GraphQL mutation methods that a user can perform.
 */
@Service
@AllArgsConstructor
public class UserMutationServiceImpl implements UserMutationService {

    /**
     * The repository for {@link Notification} entity.
     */
    private final NotificationRepository notificationRepository;
    /**
     * The repository for {@link Office} entity.
     */
    private final OfficeRepository officeRepository;
    /**
     * The repository for {@link RequestType} entity.
     */
    private final RequestTypeRepository requestTypeRepository;
    /**
     * The repository for {@link Availability} entity.
     */
    private final AvailabilityRepository availabilityRepository;
    /**
     * The repository for {@link User} entity.
     */
    private final UserRepository userRepository;

    /**
     * The service that maps the {@link NotificationInputDB} DTO to the {@link Notification} entity.
     */
    private final MapNotification mapNotification;

    /**
     * Creates a notification.
     *
     * @param notificationInput contains the notification data
     * @param user              contains the user data
     * @return the created notification
     * @throws OfficeNotFoundException     if the office is not found
     * @throws InvalidRequestTypeException if the request type is not found
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public Notification createNotification(NotificationInput notificationInput, User user)
            throws OfficeNotFoundException, InvalidRequestTypeException {
        Optional<Office> office = officeRepository.findByName(notificationInput.getOfficeName());
        if (office.isEmpty()) throw new OfficeNotFoundException("Office not found");

        Optional<RequestType> requestType = requestTypeRepository.findByName(notificationInput.getRequestTypeName());
        if (requestType.isEmpty()) throw new InvalidRequestTypeException("Request type not found");

        NotificationInputDB notificationInputDB = new NotificationInputDB(
                notificationInput, user, office.get(), requestType.get()
        );
        Notification notification = mapNotification.mapNotificationInputDBToNotification(notificationInputDB);
        notificationRepository.save(notification);
        return notification;
    }

    /**
     * Deletes a notification by id
     *
     * @param notificationId contains the notification id
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public void deleteNotification(UUID notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(notificationRepository::delete);
    }

    /**
     * Modifies a notification by id
     *
     * @param notificationInput contains the notification data
     * @param notificationId    contains the notification id
     * @return the modified notification
     * @throws NotificationNotFoundException if the notification is not found
     * @throws OfficeNotFoundException       if the office is not found
     * @throws RequestTypeNotFoundException  if the request type is not found
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public Notification modifyNotification(NotificationInput notificationInput, UUID notificationId) throws NotificationNotFoundException, OfficeNotFoundException, RequestTypeNotFoundException {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isEmpty()) throw new NotificationNotFoundException("Notification not found");

        Optional<Office> office = officeRepository.findByName(notificationInput.getOfficeName());
        if (office.isEmpty()) throw new OfficeNotFoundException("Office not found");

        Optional<RequestType> requestType = requestTypeRepository.findByName(notificationInput.getRequestTypeName());
        if (requestType.isEmpty()) throw new RequestTypeNotFoundException("Request type not found");

        NotificationInputDB notificationInputDB = new NotificationInputDB(
                notificationInput,
                notification.get().getUser(),
                office.get(),
                requestType.get()
        );

        Notification notificationModified = mapNotification.mapNotificationInputDBToNotification(notificationInputDB);

        notificationModified.setId(notificationId);

        notificationRepository.save(notificationModified);
        return notificationModified;
    }

    /**
     * Creates a reservation by id, setting the availability status to {@link Status#TAKEN}
     *
     * @param availabilityId contains the availability id
     * @param user           contains the user data
     * @return the created reservation
     * @throws UserNotFoundException         if the user is not found
     * @throws AvailabilityNotFoundException if the availability is not found
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public Availability createReservation(UUID availabilityId, User user)
            throws UserNotFoundException, AvailabilityNotFoundException {

        Optional<Availability> availabilityOptional = availabilityRepository.findById(availabilityId);

        if (!userRepository.existsById(user.getId())) throw new UserNotFoundException("User not found");
        if (availabilityOptional.isEmpty()) throw new AvailabilityNotFoundException("Availability not found");

        Availability availability = availabilityOptional.get();

        availability.setStatus(Status.TAKEN);
        availability.setUser(user);

        availabilityRepository.save(availability);

        return availability;
    }

    /**
     * Deletes a reservation by id, setting the availability status to {@link Status#FREE}
     * and setting the availability user to {@literal  null}
     *
     * @param availabilityId contains the availability id
     * @throws AvailabilityNotFoundException if the availability is not found
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public void deleteReservation(UUID availabilityId) throws AvailabilityNotFoundException {
        Optional<Availability> availabilityOptional = availabilityRepository.findById(availabilityId);

        if (availabilityOptional.isEmpty()) throw new AvailabilityNotFoundException("Availability not found");

        Availability availability = availabilityOptional.get();

        availability.setStatus(Status.FREE);
        availability.setUser(null);
        availabilityRepository.save(availability);
    }
}
