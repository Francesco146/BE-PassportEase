package it.univr.passportease.repository;

import it.univr.passportease.entity.Citizen;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface CitizenRepository extends JpaRepository<Citizen, UUID> {
    void deleteByFiscalCode(String fiscalCode);

    Optional<Citizen> findByFiscalCode(String fiscalCode);
}
