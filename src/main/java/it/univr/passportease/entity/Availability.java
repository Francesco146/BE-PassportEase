package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.util.Date;

import it.univr.passportease.entity.enums.Status;

@Entity
@Table(name = "availabilities")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class Availability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NonNull
    private Date date;

    @NonNull
    private Time time;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
}
