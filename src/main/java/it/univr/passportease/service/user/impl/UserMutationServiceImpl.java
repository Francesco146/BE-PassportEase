package it.univr.passportease.service.user.impl;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.dto.input.NotificationInputDB;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.User;
import it.univr.passportease.helper.map.MapNotification;
import it.univr.passportease.repository.NotificationRepository;
import it.univr.passportease.service.user.UserMutationService;

import it.univr.passportease.repository.OfficeRepository;
import it.univr.passportease.repository.RequestTypeRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserMutationServiceImpl implements UserMutationService {

    private final NotificationRepository notificationRepository;
    private final OfficeRepository officeRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final MapNotification mapNotification;

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public Notification createNotification(NotificationInput notificationInput, User user) {
        
        Optional<Office> office = officeRepository.findByName(notificationInput.getOfficeName());
        if (office.isEmpty()) {
            throw new IllegalArgumentException("Office not found");
        }

        Optional<RequestType> requestType = requestTypeRepository.findByName(notificationInput.getRequestTypeName());
        if (requestType.isEmpty()) {
            throw new IllegalArgumentException("Request type not found");
        }

        NotificationInputDB notificationInputDB = new NotificationInputDB(notificationInput, user, office.get(), requestType.get());
        Notification notification = mapNotification.mapNotificationInputDBToNotification(notificationInputDB);
        notificationRepository.save(notification);
        return notification;
    }
}
