package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

/**
 * The {@code Request} class is an entity that represents a request made by a {@link Worker} to the {@link
 * RequestType} of his choice. It contains the {@link Worker} that made the request, the {@link RequestType} of the
 * request, the duration of the request, the start and end date and time of the request, and the creation and update
 * date of the request.
 */
@Entity
@Table(name = "requests")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Request {

    /**
     * The unique identifier of the {@link Request}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The duration of the {@link Request}.
     */
    private long duration;

    /**
     * The start date of the {@link Request}.
     */
    @NonNull
    @Column(name = "start_date")
    private Date startDate;

    /**
     * The end date of the {@link Request}.
     */
    @NonNull
    @Column(name = "end_date")
    private Date endDate;

    /**
     * The start time of the {@link Request}.
     */
    @NonNull
    @Column(name = "start_time")
    private LocalTime startTime;

    /**
     * The end time of the {@link Request}.
     */
    @NonNull
    @Column(name = "end_time")
    private LocalTime endTime;

    /**
     * The {@link Worker} that made the {@link Request}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    /**
     * The {@link RequestType} of the {@link Request}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "request_type_id")
    private RequestType requestType;

    /**
     * The creation date of the {@link Request}.
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The last update date of the {@link Request}.
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for {@link Request}.
     *
     * @param o The object to compare with this {@code Request}.
     * @return {@code true} if the given object is an instance of {@code Request} and has the same {@code id} as this
     * {@code Request}, {@code false} otherwise.
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

        if (o instanceof Request request)
            return getId().equals(request.getId());
        return false;
    }

    /**
     * Hash code method for {@link Request}.
     *
     * @return The hash code of this {@code Request}.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
