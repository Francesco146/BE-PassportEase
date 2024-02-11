package it.univr.passportease.repository;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.User;
import it.univr.passportease.entity.enums.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Availability} entity.
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID>, JpaSpecificationExecutor<Availability> {
    /**
     * Find the {@link Availability} entities that satisfy the given {@link Specification}.
     *
     * @param specification must not be {@literal null}. It is used to filter the results of the query.
     * @return a list of {@link Availability} entities that satisfy the given {@link Specification}.
     */
    @NotNull
    List<Availability> findAll(@NotNull Specification<Availability> specification);

    /**
     * Find the {@link Availability} entities that satisfy the given {@link Specification} and have the given
     * {@link Request} id.
     *
     * @param requestId the id of the {@link Request} entity.
     * @return a list of {@link Availability} entities that satisfy the given {@link Specification}.
     */
    List<Availability> findByRequestId(UUID requestId);

    /**
     * Find the list of {@link Availability} entities that have the given {@link User} id.
     *
     * @param user the {@link User} entity.
     * @return a list of {@link Availability} entities of the given {@link User}.
     */
    List<Availability> findByUser(User user);


    /**
     * Find the list of {@link Availability} entities that have the given status.
     *
     * @param status the status of the {@link Availability} entity.
     * @return a list of {@link Availability} entities of the given status.
     */
    List<Availability> findByStatus(Status status);

    List<Availability> findAllByUserAndStatus(User user, Status status);
}
