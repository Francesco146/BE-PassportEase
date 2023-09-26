package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "requests")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private long duration;

    @NonNull
    @Column(name = "start_date")
    private Date startDate;

    @NonNull
    @Column(name = "end_date")
    private Date endDate;

    @NonNull
    @Column(name = "start_time")
    private LocalTime startTime;

    @NonNull
    @Column(name = "end_time")
    private LocalTime endTime;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "request_type_id")
    private RequestType requestType;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

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
            return getId() != null && Objects.equals(getId(), request.getId());
        return false;
    }

    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
