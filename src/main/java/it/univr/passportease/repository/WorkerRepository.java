package it.univr.passportease.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Worker;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, UUID> {
    Optional<Worker> findById(UUID id);
    Integer countByOffice(Office office);
}