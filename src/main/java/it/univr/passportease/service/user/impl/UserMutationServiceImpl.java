package it.univr.passportease.service.user.impl;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.dto.input.NotificationInputDB;
import it.univr.passportease.entity.*;
import it.univr.passportease.entity.enums.Status;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.*;
import it.univr.passportease.helper.map.MapNotification;
import it.univr.passportease.repository.*;
import it.univr.passportease.service.user.UserMutationService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
     * @throws UserNotFoundException          if the user is not found
     * @throws AvailabilityNotFoundException  if the availability is not found
     * @throws InvalidAvailabilityIDException if the availability 'ritiro passaporto' date is not valid, or if the
     *                                        availability is already taken.
     *                                        It must be at least one month after the last rilascio passaporto
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public Availability createReservation(UUID availabilityId, User user)
            throws UserNotFoundException, AvailabilityNotFoundException, InvalidAvailabilityIDException {

        Optional<Availability> availabilityOptional = availabilityRepository.findById(availabilityId);

        if (!userRepository.existsById(user.getId())) throw new UserNotFoundException("User not found");
        if (availabilityOptional.isEmpty()) throw new AvailabilityNotFoundException("Availability not found");

        Availability availabilityRequested = availabilityOptional.get();

        if (availabilityRequested.getStatus() == Status.TAKEN)
            throw new InvalidAvailabilityIDException("Availability already taken");

        boolean isRitiroPassaporto = availabilityRequested
                .getRequest()
                .getRequestType()
                .getName()
                .equals("ritiro passaporto");

        ArrayList<Availability> rilascioPassaportiOfUser = availabilityRepository
                .findByUser(user)
                .stream()
                .filter(availability -> availability.getStatus() == Status.TAKEN)
                .filter(availability -> availability.getRequest()
                        .getRequestType()
                        .getName()
                        .equals("rilascio passaporto"))
                .sorted((availability1, availability2) ->
                        availability2.getDate().compareTo(availability1.getDate()))
                .collect(Collectors.toCollection(ArrayList::new));

        Date rilascioDatePlusOneMonth = DateUtils
                .addMonths(
                        rilascioPassaportiOfUser
                                .getLast()
                                .getDate(),
                        1
                );

        boolean isDateValid = !rilascioPassaportiOfUser.isEmpty() &&
                availabilityRequested
                        .getDate()
                        .after(rilascioDatePlusOneMonth);


        if (isRitiroPassaporto && !isDateValid)
            throw new InvalidAvailabilityIDException("Availability not valid for the request");


        availabilityRequested.setStatus(Status.TAKEN);
        availabilityRequested.setUser(user);

        availabilityRepository.save(availabilityRequested);

        return availabilityRequested;
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

    /**
     * Preserves the availability by id, setting the availability status to {@link Status#PENDING}
     *
     * @param availabilityId contains the availability id
     * @throws AvailabilityNotFoundException if the availability is not found
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public void preserveAvailability(UUID availabilityId) throws AvailabilityNotFoundException {
        Optional<Availability> availabilityOptional = availabilityRepository.findById(availabilityId);
        if (availabilityOptional.isEmpty()) throw new AvailabilityNotFoundException("Availability not found");
        Availability availability = availabilityOptional.get();
        availability.setStatus(Status.PENDING);
        availabilityRepository.save(availability);
    }

    /**
     * This method is scheduled to run every 10 minutes.
     * It restores the availability status to {@link Status#FREE} and sets the availability user to {@literal null}
     * for all the availabilities with status {@link Status#PENDING}
     */
    @Scheduled(cron = "0 */20 * * * *") // every 20 minutes
    public void restoreAvailability() {
        availabilityRepository
                .findByStatus(Status.PENDING)
                .forEach(availability -> {
                            availability.setStatus(Status.FREE);
                            availability.setUser(null);
                            availabilityRepository.save(availability);
                        }
                );
    }
}
