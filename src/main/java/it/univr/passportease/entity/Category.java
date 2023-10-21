package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.UUID;

/**
 * The {@link Category} entity that represents a category of a {@link Citizen}.
 */
@Entity
@Table(name = "categories")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Category {

    /**
     * The unique identifier of this {@link Category}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The name of this {@link Category}.
     */
    @NonNull
    @Column(unique = true)
    private String name;

    /**
     * The description of this {@link Category}.
     */
    private String description;

    /**
     * The {@link Date} of creation of this {@link Category}.
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The {@link Date} of last update of this {@link Category}.
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for this {@link Category}.
     *
     * @param o The object to compare with this {@link Category}.
     * @return {@code true} if the given object is an instance of {@link Category} and has the same {@link UUID} as this
     * {@link Category}, {@code false} otherwise.
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

        if (o instanceof Category category)
            return getId().equals(category.getId());
        return false;
    }

    /**
     * Hash code method for this {@link Category}.
     *
     * @return The hash code of this {@link Category}.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
