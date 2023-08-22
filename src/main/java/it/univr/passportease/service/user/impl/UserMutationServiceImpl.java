package it.univr.passportease.service.user.impl;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.dto.input.NotificationInputDB;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.User;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.OfficeNotFoundException;
import it.univr.passportease.helper.map.MapNotification;
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
}
