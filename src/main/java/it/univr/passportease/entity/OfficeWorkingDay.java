package it.univr.passportease.entity;

import it.univr.passportease.entity.enums.Day;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

/**
 * This class represents the working days of an office, which are defined by the day of the week, the start time and
 * the end time. The start time and the end time are defined by two time intervals, which are optional. If the office
 * is open only in the morning, the second time interval is not defined. If the office is open only in the afternoon,
 * the first time interval is not defined. If the office is open all day, both time intervals are defined.
 */
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

    /**
     * @param o The object to compare with this object.
     * @return {@code true} if the given object is an instance of {@link OfficeWorkingDay} and has the same id of this
     * object, {@code false} otherwise.
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

        if (o instanceof OfficeWorkingDay officeWorkingDay)
            return getId().equals(officeWorkingDay.getId());
        return false;
    }

    /**
     * @return The hash code of this object.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
