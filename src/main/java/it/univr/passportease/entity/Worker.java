package it.univr.passportease.entity;

import it.univr.passportease.helper.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.UUID;

/**
 * The {@code Worker} class is an entity that represents a worker. The worker is
 * the person who is responsible for the management of the offices.
 */
@Entity
@RequiredArgsConstructor
@Table(name = "workers")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Worker implements UserType {

    /**
     * The id of the {@link Worker}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The username of the {@link Worker}.
     */
    @NonNull
    @Column(unique = true)
    private String username;

    /**
     * The email of the {@link Worker}.
     */
    @NonNull
    @Column(unique = true)
    private String email;

    /**
     * The hash password of the {@link Worker}.
     * Known issue: The password is not salted or peppered.
     */
    @NonNull
    @Column(name = "hash_password")
    private String hashPassword;

    /**
     * The refresh token of the {@link Worker}.
     * Known issue: The refresh token is stored in plain text, and if the database is compromised, the attacker can
     * impersonate the worker.
     */
    @NonNull
    @Column(name = "refresh_token", length = 1024)
    private String refreshToken;

    /**
     * The office of the {@link Worker}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    /**
     * The {@link Date} of creation of the {@link Worker}
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The {@link Date} of the last update of the {@link Worker}
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for the {@link Worker} class.
     *
     * @param o The object to compare.
     * @return {@code true} if the objects have the same id, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Class<?> oEffectiveClass;
        Class<?> thisEffectiveClass;

        if (o instanceof HibernateProxy oInstance)
            oEffectiveClass = oInstance.getHibernateLazyInitializer().getPersistentClass();
        else
            oEffectiveClass = o.getClass();

        if (this instanceof HibernateProxy thisInstance)
            thisEffectiveClass = thisInstance.getHibernateLazyInitializer().getPersistentClass();
        else
            thisEffectiveClass = this.getClass();

        if (thisEffectiveClass != oEffectiveClass) return false;

        if (o instanceof Worker worker)
            return getId().equals(worker.getId());
        return false;
    }

    /**
     * Hash code method for the {@link Worker} class.
     *
     * @return The hash code of the id.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
