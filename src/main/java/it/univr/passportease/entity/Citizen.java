package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "citizens")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NonNull
    @Column(name = "fiscal_code", unique = true)
    private String fiscalCode;

    @NonNull
    private String name;

    @NonNull
    private String surname;

    @NonNull
    @Column(name = "city_of_birth")
    private String cityOfBirth;

    @NonNull
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @NonNull
    @Column(name = "health_card_number")
    private String healthCardNumber;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
