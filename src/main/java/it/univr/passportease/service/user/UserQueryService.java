package it.univr.passportease.service.user;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserQueryService {
    User getUserDetails(String token);

    List<Notification> getUserNotifications(String token);

    List<Availability> getUserReservations(String token);

}
