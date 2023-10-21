package it.univr.passportease.repository;

import it.univr.passportease.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for the {@link Availability} entity.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Availability, UUID> {

    /**
     * Finds all the {@link Availability} entities associated to the given user.
     *
     * @param id the id of the user
     * @return the list of {@link Availability} entities associated to the given user
     */
    List<Availability> findByUserId(UUID id);
}
