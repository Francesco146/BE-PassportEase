package it.univr.passportease.repository;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Worker;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface WorkerRepository extends JpaRepository<Worker, UUID> {
    @NotNull
    Optional<Worker> findById(@NotNull UUID id);

    long countByOffice(Office office);

    Optional<Worker> findByUsername(String username);

    boolean existsById(@NotNull UUID id);
}