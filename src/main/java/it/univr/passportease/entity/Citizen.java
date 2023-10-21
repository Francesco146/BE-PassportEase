package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.UUID;

/**
 * The {@link Citizen} class is an entity that represents a citizen. It is
 * used as a white list for the users that can access the system.
 */
@Entity
@Table(name = "citizens")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Citizen {

    /**
     * The unique identifier of this {@link Citizen}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The fiscal code of this {@link Citizen}.
     */
    @NonNull
    @Column(name = "fiscal_code", unique = true)
    private String fiscalCode;

    /**
     * The name of this {@link Citizen}.
     */
    @NonNull
    private String name;

    /**
     * The surname of this {@link Citizen}.
     */
    @NonNull
    private String surname;

    /**
     * The city of birth of this {@link Citizen}.
     */
    @NonNull
    @Column(name = "city_of_birth")
    private String cityOfBirth;

    /**
     * The date of birth of this {@link Citizen}.
     */
    @NonNull
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    /**
     * The health card number of this {@link Citizen}.
     */
    @NonNull
    @Column(name = "health_card_number")
    private String healthCardNumber;

    /**
     * The {@link Date} of creation of this {@link Citizen}.
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The {@link Date} of last update of this {@link Citizen}.
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for this {@link Citizen}.
     *
     * @param o The object to compare with this {@link Citizen}.
     * @return {@code true} if the given object is an instance of {@link Citizen} and
     * if it has the same {@link UUID} as this {@link Citizen}.
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

        if (o instanceof Citizen citizen)
            return getId().equals(citizen.getId());
        return false;
    }

    /**
     * Hash code method for this {@link Citizen}.
     *
     * @return The hash code of this {@link Citizen}.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
