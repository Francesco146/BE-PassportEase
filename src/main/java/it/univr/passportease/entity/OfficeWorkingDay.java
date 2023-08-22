package it.univr.passportease.entity;

import it.univr.passportease.entity.enums.Day;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "office_working_days")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OfficeWorkingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Day day;

    @NonNull
    @Column(name = "start_time1")
    private LocalTime startTime1;

    @NonNull
    @Column(name = "end_time1")
    private LocalTime endTime1;

    @Column(name = "start_time2")
    private LocalTime startTime2;

    @Column(name = "end_time2")
    private LocalTime endTime2;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Class<?> oEffectiveClass = o instanceof HibernateProxy oInstance ?
                oInstance.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy thisInstance ?
                thisInstance.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;

        if (o instanceof OfficeWorkingDay officeWorkingDay)
            return getId() != null && Objects.equals(getId(), officeWorkingDay.getId());
        return false;
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
