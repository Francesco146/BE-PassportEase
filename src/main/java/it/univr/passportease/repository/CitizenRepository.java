package it.univr.passportease.repository;

import it.univr.passportease.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, UUID> {
    Optional<Citizen> findByFiscalCode(String fiscalCode);
}
