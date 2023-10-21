package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.UUID;

/**
 * The {@link CitizenCategory} entity that represents a category of a {@link Citizen}.
 */
@Entity
@Table(name = "citizens_categories")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CitizenCategory {

    /**
     * The unique identifier of this {@link CitizenCategory}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The {@link Citizen} of this {@link CitizenCategory}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "citizen_id")
    private Citizen citizen;

    /**
     * The {@link Category} of this {@link CitizenCategory}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * The date when this {@link CitizenCategory} was created.
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The date when this {@link CitizenCategory} was last updated.
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for this {@link CitizenCategory}.
     *
     * @param o The object to compare with this {@link CitizenCategory}.
     * @return {@code true} if the given object is an instance of {@link CitizenCategory} and has the same {@link UUID} as this
     * {@link CitizenCategory}, {@code false} otherwise.
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

        if (o instanceof CitizenCategory citizenCategory)
            return getId().equals(citizenCategory.getId());
        return false;
    }

    /**
     * Hash code method for this {@link CitizenCategory}.
     *
     * @return The hash code of this {@link CitizenCategory}.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
