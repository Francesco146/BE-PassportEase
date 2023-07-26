package it.univr.passportease.entity;

import lombok.*;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"fiscalCode"})
public class User {
    
    @Id
    private String fiscalCode;

    private String email;

    private String name;

    private String surname;

    private String cityOfBirth;

    private Date dateOfBirth;

    private String hashPassword;

    private Boolean active;

    private String refreshToken;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;
}
