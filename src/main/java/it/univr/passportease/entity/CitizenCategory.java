package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "citizens_categories")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class CitizenCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NonNull
    @ManyToOne
    @JoinColumn(name = "citizen_id")
    private Citizen citizen;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
}
