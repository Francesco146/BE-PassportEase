package it.univr.passportease.service.user;

import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;

import java.util.List;

public interface UserQueryService {
    User getUserDetails(String token);

    List<Notification> getUserNotifications(String token);
}
