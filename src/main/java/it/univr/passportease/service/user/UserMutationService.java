package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.OfficeNotFoundException;

import java.util.UUID;

public interface UserMutationService {
    Notification createNotification(NotificationInput notificationInput, User user) throws OfficeNotFoundException, InvalidRequestTypeException;

    void deleteNotification(UUID notificationId);
}
