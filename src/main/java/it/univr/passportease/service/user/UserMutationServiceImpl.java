package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.helper.map.MapUser;
import it.univr.passportease.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserMutationServiceImpl implements UserMutationService {

    private final NotificationRepository notificationRepository;
    private final MapUser mapUser;

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public Notification createNotification(NotificationInput notificationInput, User user) {
        Notification notification = mapUser.mapNotificationInputToNotification(notificationInput, user);
        notificationRepository.save(notification);
        return notification;
    }
}
