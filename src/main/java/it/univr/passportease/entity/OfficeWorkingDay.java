package it.univr.passportease.entity;

import it.univr.passportease.entity.enums.Day;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Time;
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
    private Time startTime1;

    @NonNull
    @Column(name = "end_time1")
    private Time endTime1;

    @Column(name = "start_time2")
    private Time startTime2;

    @Column(name = "end_time2")
    private Time endTime2;

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
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OfficeWorkingDay that = (OfficeWorkingDay) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
