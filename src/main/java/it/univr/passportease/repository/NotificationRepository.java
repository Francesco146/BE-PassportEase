package it.univr.passportease.repository;

import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.RequestType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserId(UUID id);
    List<Notification> findAllByOfficeAndIsReadyAndRequestType(Office office, boolean isReady, RequestType requestType);
}
