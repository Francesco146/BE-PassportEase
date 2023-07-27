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
@EqualsAndHashCode(of = { "id" })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Column(name = "fiscal_code", unique = true)
    private String fiscalCode;

    @NonNull
    @Column(unique = true)
    private String email;

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
    @Column(name = "hash_password")
    private String hashPassword;

    @NonNull
    private Boolean active;

    @NonNull
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
