package it.univr.passportease.helper.map;

import java.util.Date;

import org.springframework.stereotype.Component;

import it.univr.passportease.dto.input.NotificationInputDB;
import it.univr.passportease.entity.Notification;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class MapNotification {
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
