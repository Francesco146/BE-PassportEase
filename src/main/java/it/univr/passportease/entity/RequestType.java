package it.univr.passportease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.UUID;

/**
 * The type Request type entity. Used to store the types of requests that can be made by the workers.
 */
@Entity
@Table(name = "request_types")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RequestType {

    /**
     * Unique identifier of the {@link RequestType}
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The name of the {@link RequestType}
     */
    @NonNull
    @Column(unique = true)
    private String name;

    /**
     * A boolean value that indicates if the {@link RequestType} has a dependency. If true, the {@link RequestType} can
     * be executed only if the {@link RequestType} on which it depends has been executed by the {@link User}
     */
    @Column(name = "has_dependency")
    private boolean hasDependency;

    /**
     * The {@link Date} of creation of the {@link RequestType}
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The {@link Date} of the last update of the {@link RequestType}
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Two {@link RequestType} are equals if they have the same id.
     *
     * @param o The object to compare
     * @return True if the objects have the same id, false otherwise.
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

        if (o instanceof RequestType requestType)
            return getId().equals(requestType.getId());
        return false;
    }

    /**
     * The hash code of a {@link RequestType} is calculated using the id.
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
