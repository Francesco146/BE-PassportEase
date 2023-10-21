package it.univr.passportease.repository;

import it.univr.passportease.entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for the {@link RequestType} entity.
 */
@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType, UUID> {
    /**
     * Finds the {@link RequestType} with the given name.
     *
     * @param name the name of the {@link RequestType} to find
     * @return the {@link RequestType} with the given name, as an {@link Optional}
     */
    Optional<RequestType> findByName(String name);

}
