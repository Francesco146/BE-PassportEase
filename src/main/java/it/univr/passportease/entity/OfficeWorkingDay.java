package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.util.Date;

import it.univr.passportease.entity.enums.Day;

@Entity
@Table(name = "office_working_days")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class OfficeWorkingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
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
}
