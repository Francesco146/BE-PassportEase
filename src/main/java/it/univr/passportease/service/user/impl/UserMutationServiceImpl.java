package it.univr.passportease.service.user.impl;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.dto.input.NotificationInputDB;
import it.univr.passportease.entity.*;
import it.univr.passportease.entity.enums.Status;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.AvailabilityNotFoundException;
import it.univr.passportease.exception.notfound.NotificationNotFoundException;
import it.univr.passportease.exception.notfound.OfficeNotFoundException;
import it.univr.passportease.exception.notfound.RequestTypeNotFoundException;
import it.univr.passportease.helper.map.MapNotification;
import it.univr.passportease.repository.AvailabilityRepository;
import it.univr.passportease.repository.NotificationRepository;
import it.univr.passportease.repository.OfficeRepository;
import it.univr.passportease.repository.RequestTypeRepository;
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
    private final MapNotification mapNotification;
    private final AvailabilityRepository availabilityRepository;

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
    public Availability createReservation(UUID availabilityId) {
        Optional<Availability> availability = availabilityRepository.findById(availabilityId);
        if (availability.isEmpty()) throw new AvailabilityNotFoundException("Availability not found");

        availability.get().setStatus(Status.TAKEN);
        availabilityRepository.save(availability.get());

        return availability.get();
    }
}
