package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.UUID;

/**
 * The {@code User} class is an entity that represents a user, or costumers, of the application.
 */
@Entity
@RequiredArgsConstructor
@Table(name = "users")
@NoArgsConstructor(force = true)
@Getter
@Setter
@ToString
public class User {

    /**
     * The unique identifier of the {@link User}
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The unique fiscal code of the {@link User}
     */
    @NonNull
    @Column(name = "fiscal_code", unique = true)
    private String fiscalCode;

    /**
     * The unique email of the {@link User}
     */
    @NonNull
    @Column(unique = true)
    private String email;

    /**
     * The name of the {@link User}
     */
    @NonNull
    private String name;

    /**
     * The surname of the {@link User}
     */
    @NonNull
    private String surname;

    /**
     * The city of birth of the {@link User}
     */
    @NonNull
    @Column(name = "city_of_birth")
    private String cityOfBirth;

    /**
     * The date of birth of the {@link User}
     */
    @NonNull
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    /**
     * The hash password of the {@link User}.
     * Known issue: The password is not salted or peppered.
     */
    @NonNull
    @Column(name = "hash_password")
    private String hashPassword;

    /**
     * A boolean that indicates if the {@link User} is active or not
     */
    @NonNull
    private Boolean active;

    /**
     * The refresh token of the {@link User}.
     * Known issue: The refresh token is stored in plain text, and if the database is compromised, the attacker can
     * impersonate the user.
     */
    @NonNull
    @Column(name = "refresh_token", length = 1024)
    private String refreshToken;

    /**
     * The creation date of the {@link User}
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The last update date of the {@link User}
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for the {@link User} class
     *
     * @param o the object to compare
     * @return {@code true} if the object is equals to this, {@code false} otherwise
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

        if (o instanceof User user)
            return getId().equals(user.getId());
        return false;
    }

    /**
     * Hash code method for the {@link User} class
     *
     * @return the hash code of this object
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
