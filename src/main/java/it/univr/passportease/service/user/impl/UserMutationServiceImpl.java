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

@Service
@AllArgsConstructor
public class UserMutationServiceImpl implements UserMutationService {

    private final NotificationRepository notificationRepository;
    private final OfficeRepository officeRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    private final MapNotification mapNotification;

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

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public void deleteNotification(UUID notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(notificationRepository::delete);
    }

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

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public Availability createReservation(UUID availabilityId, User user)
            throws AvailabilityNotFoundException, UserNotFoundException {

        Optional<Availability> availabilityOptional = availabilityRepository.findById(availabilityId);

        if (userRepository.existsById(user.getId())) throw new UserNotFoundException("User not found");
        if (availabilityOptional.isEmpty()) throw new AvailabilityNotFoundException("Availability not found");

        Availability availability = availabilityOptional.get();

        availability.setStatus(Status.TAKEN);
        availability.setUser(user);

        availabilityRepository.save(availability);

        return availability;
    }

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
