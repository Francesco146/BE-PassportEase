package it.univr.passportease.repository;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Worker;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for the {@link Worker} entity.
 */
@Repository
public interface WorkerRepository extends JpaRepository<Worker, UUID> {
    /**
     * Finds a {@link Worker} by its id.
     *
     * @param id the id of the {@link Worker} to find.
     * @return an {@link Optional} containing the {@link Worker} if found, an empty {@link Optional} otherwise.
     */
    @NotNull
    Optional<Worker> findById(@NotNull UUID id);

    /**
     * @param office the {@link Office} to count the workers of.
     * @return the number of workers of the given {@link Office}.
     */
    long countByOffice(Office office);

    /**
     * @param username the username of the {@link Worker} to find.
     * @return an {@link Optional} containing the {@link Worker} if found, an empty {@link Optional} otherwise.
     */
    Optional<Worker> findByUsername(String username);

}