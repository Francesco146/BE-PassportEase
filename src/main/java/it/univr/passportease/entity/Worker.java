package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "workers")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    @Column(name = "hash_password")
    private String hashPassword;

    @NonNull
    @Column(name = "refresh_token")
    private String refreshToken;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
