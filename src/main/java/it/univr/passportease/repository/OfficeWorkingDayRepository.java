package it.univr.passportease.repository;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.OfficeWorkingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link OfficeWorkingDay} entity.
 */
@Repository
public interface OfficeWorkingDayRepository extends JpaRepository<OfficeWorkingDay, UUID> {

    /**
     * @param office {@link Office} entity
     * @return {@link List} of {@link OfficeWorkingDay} entities
     */
    List<OfficeWorkingDay> findByOffice(Office office);

}
