package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "notifications")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Column(name = "is_ready")
    private Boolean isReady;

    @NonNull
    @Column(name = "start_date")
    private Date startDate;

    @NonNull
    @Column(name = "end_date")
    private Date endDate;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "request_type_id")
    private RequestType requestType;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

}
