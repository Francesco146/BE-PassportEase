package it.univr.passportease.repository;

import it.univr.passportease.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfficeRepository extends JpaRepository<Office, UUID> {

    Optional<Office> findByName(String name);
}
