package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.UUID;

/**
 * The {@link  Office} class is an entity that represents an office, which is a place where
 * {@link Worker workers} can manages {@link Request} requests.
 */
@Entity
@Table(name = "offices")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Office {

    /**
     * The unique identifier of this {@link Office}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The name of this {@link Office}.
     */
    @NonNull
    @Column(unique = true)
    private String name;

    /**
     * The address of this {@link Office}.
     */
    @NonNull
    private String address;

    /**
     * The {@link Date} when this {@link Office} was created.
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The {@link Date} when this {@link Office} was last updated.
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for {@link Office} class.
     *
     * @param o The object to compare with this {@link Office}.
     * @return {@code true} if the given object is an instance of {@link Office} and has the same
     * {@link UUID} as this {@link Office}, {@code false} otherwise.
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

        if (o instanceof Office office)
            return getId().equals(office.getId());
        return false;
    }

    /**
     * Hash code method for {@link Office} class.
     *
     * @return The hash code of this {@link Office}.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
