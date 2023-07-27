package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "requests_offices")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class RequestOffice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
}
