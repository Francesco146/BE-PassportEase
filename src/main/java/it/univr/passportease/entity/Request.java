package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "requests")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class Request {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NonNull
    private Integer duration;

    @NonNull
    @Column(name = "starting_date")
    private Date startingDate;

    @NonNull
    @Column(name = "ending_date")
    private Date endingDate;

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
}
