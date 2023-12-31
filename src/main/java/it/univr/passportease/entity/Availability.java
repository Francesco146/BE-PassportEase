package it.univr.passportease.entity;

import it.univr.passportease.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

/**
 * The {@code Availability} class is an entity model object. It represents the availability of an {@link Office} for
 * a specific {@link Request} at a specific {@link Date} and {@link LocalTime}.
 */
@Entity
@Table(name = "availabilities")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Availability {

    /**
     * The id of the {@link Availability}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The status of the {@link Availability}.
     */
    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * The date of the {@link Availability}.
     */
    @NonNull
    private Date date;

    /**
     * The time of the {@link Availability}.
     */
    @NonNull
    private LocalTime time;

    /**
     * The {@link Request} of the {@link Availability}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    /**
     * The {@link Office} of the {@link Availability}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    /**
     * The {@link User} of the {@link Availability}.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The creation {@link Date} of the {@link Availability}.
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The update {@link Date} of the {@link Availability}.
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for {@link Availability}.
     *
     * @param o The object to compare with this {@link  Availability}.
     * @return {@code  true} if the given object is an instance of {@link  Availability} and has the same {@code id} as
     * this {@link Availability}, {@code false} otherwise.
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

        if (o instanceof Availability availability)
            return getId().equals(availability.getId());
        return false;
    }

    /**
     * Hash code method for {@link Availability}.
     *
     * @return The hash code of this {@link Availability}.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
