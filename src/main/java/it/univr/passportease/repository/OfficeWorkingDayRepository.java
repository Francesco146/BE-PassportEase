package it.univr.passportease.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.OfficeWorkingDay;

@Repository
public interface OfficeWorkingDayRepository extends JpaRepository<OfficeWorkingDay, UUID>{

    List<OfficeWorkingDay> findByOffice(Office office);
    
}
