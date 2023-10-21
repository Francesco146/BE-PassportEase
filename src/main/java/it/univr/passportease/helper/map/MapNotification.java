package it.univr.passportease.helper.map;

import it.univr.passportease.dto.input.NotificationInputDB;
import it.univr.passportease.entity.Notification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Map {@link NotificationInputDB} to {@link Notification}
 */
@AllArgsConstructor
@Component
public class MapNotification {
    /**
     * Map {@link NotificationInputDB} to {@link Notification}
     *
     * @param notificationInputDB {@link NotificationInputDB} to map
     * @return {@link Notification} mapped
     */
    public Notification mapNotificationInputDBToNotification(NotificationInputDB notificationInputDB) {
        Notification notification = new Notification();
        notification.setIsReady(false);
        notification.setStartDate(notificationInputDB.getNotificationInput().getStartDate());
        notification.setEndDate(notificationInputDB.getNotificationInput().getEndDate());
        notification.setOffice(notificationInputDB.getOffice());
        notification.setUser(notificationInputDB.getUser());
        notification.setRequestType(notificationInputDB.getRequestType());
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());
        return notification;
    }
}
