package it.univr.passportease.repository;

import it.univr.passportease.entity.Citizen;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Citizen} entity.
 */
@Repository
@Transactional
public interface CitizenRepository extends JpaRepository<Citizen, UUID> {
    /**
     * @param fiscalCode the fiscal code of the citizen to delete
     */
    void deleteByFiscalCode(String fiscalCode);

    /**
     * @param fiscalCode the fiscal code of the citizen to find
     * @return an {@link Optional} containing the citizen with the given fiscal code
     */
    Optional<Citizen> findByFiscalCode(String fiscalCode);
}
