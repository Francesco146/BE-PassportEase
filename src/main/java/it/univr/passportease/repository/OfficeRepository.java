package it.univr.passportease.repository;

import it.univr.passportease.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {
    Optional<Office> findByName(String name);
    List<Office> findAllByNameIn(List<String> names);
}
