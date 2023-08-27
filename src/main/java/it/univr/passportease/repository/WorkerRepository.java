package it.univr.passportease.repository;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Worker;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkerRepository extends JpaRepository<Worker, UUID> {
    @NotNull
    Optional<Worker> findById(@NotNull UUID id);

    long countByOffice(Office office);

    Optional<Worker> findByUsername(String username);
}