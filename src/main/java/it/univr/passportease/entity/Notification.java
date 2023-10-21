package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.UUID;

/**
 * The {@link Notification} entity that represents a notification that can be requested by a {@link User} to a {@link Office}.
 */
@Entity
@Table(name = "notifications")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Notification {

    /**
     * The unique identifier of this {@link Notification}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * A boolean value that indicates whether this {@link Notification} is ready to be collected by the {@link User}.
     */
    @NonNull
    @Column(name = "is_ready")
    private Boolean isReady;

    /**
     * The message of this {@link Notification}.
     */
    private String message;

    /**
     * The start date of this {@link Notification}, that is the date of when the {@link User} wants to start the notification.
     */
    @NonNull
    @Column(name = "start_date")
    private Date startDate;

    /**
     * The end date of this {@link Notification}, that is the date of when the {@link User} wants to end the notification.
     */
    @NonNull
    @Column(name = "end_date")
    private Date endDate;

    /**
     * The {@link Office} that this {@link Notification} is requested to.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    /**
     * The {@link User} that requested this {@link Notification}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The {@link RequestType} of this {@link Notification}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "request_type_id")
    private RequestType requestType;

    /**
     * The {@link Date} of when this {@link Notification} was created.
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The {@link Date} of when this {@link Notification} was updated.
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for this {@link Notification}.
     *
     * @param o The object to compare with this {@link Notification}.
     * @return {@code true} if the given object is a {@link Notification} and has the same {@link UUID} as this {@link Notification}.
     * {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Class<?> oEffectiveClass;
        Class<?> thisEffectiveClass;

        if (o instanceof HibernateProxy oInstance)
            oEffectiveClass = oInstance.getHibernateLazyInitializer().getPersistentClass();
        else
            oEffectiveClass = o.getClass();

        if (this instanceof HibernateProxy thisInstance)
            thisEffectiveClass = thisInstance.getHibernateLazyInitializer().getPersistentClass();
        else
            thisEffectiveClass = this.getClass();

        if (thisEffectiveClass != oEffectiveClass) return false;

        if (o instanceof Notification notification)
            return getId().equals(notification.getId());
        return false;
    }

    /**
     * Hash code method for this {@link Notification}.
     *
     * @return The hash code of this {@link Notification}.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
