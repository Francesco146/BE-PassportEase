package it.univr.passportease.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.univr.passportease.entity.Citizen;

import java.util.Optional;
import java.util.UUID;

public interface CitizenRepository extends JpaRepository<Citizen, UUID>{
    Optional<Citizen> findByFiscalCode(String fiscalCode);
}
