package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;

public interface UserMutationService {
    Notification createNotification(NotificationInput notificationInput, User user);
}
