package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.UUID;

/**
 * The {@code RequestOffice} class is an entity that represents the relationship between a {@link Request} and an
 * {@link Office}.
 */
@Entity
@Table(name = "requests_offices")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RequestOffice {

    /**
     * The unique identifier of this {@link RequestOffice}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The {@link Office} that is related to this {@link RequestOffice}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    /**
     * The {@link Request} that is related to this {@link RequestOffice}.
     */
    @NonNull
    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    /**
     * The date and time when this {@link RequestOffice} was created.
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The date and time when this {@link RequestOffice} was last updated.
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Equals method for this {@link RequestOffice}.
     *
     * @param o The object to compare.
     * @return {@code true} if the given object is an instance of {@link RequestOffice} and has the same {@code id} as
     * this one, {@code false} otherwise.
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

        if (o instanceof RequestOffice requestOffice)
            return getId().equals(requestOffice.getId());
        return false;
    }

    /**
     * Hash code method for this {@link RequestOffice}.
     *
     * @return The hash code of this object.
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy)
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        return getClass().hashCode();
    }
}
