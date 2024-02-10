package it.univr.passportease.repository;

import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for the {@link Notification} entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    /**
     * Find all the notifications for the user with the given id.
     *
     * @param id the id of the user
     * @return the list of notifications for the user with the given id
     */
    List<Notification> findByUserId(UUID id);

    /**
     * Find all the notifications for the given office, ready status and request type.
     *
     * @param office      the office related to the notification to find
     * @param isReady     filter for the ready status of the notification
     * @param requestType the type of the request related to the notification to find
     * @return the list of notifications for the given office, ready status and request type
     */
    List<Notification> findAllByOfficeAndIsReadyAndRequestType(Office office, boolean isReady, RequestType requestType);

    List<Notification> findAllByRequestType(RequestType requestType);
}
