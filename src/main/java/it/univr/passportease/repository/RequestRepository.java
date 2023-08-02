package it.univr.passportease.repository;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
    @Query(value = "SELECT COUNT(*) " +
            "FROM requests r, requests_offices ro, offices o " +
            "WHERE r.id = ro.request_id" +
            "AND ro.office_id = ?1"+
            "AND r.start_date >= ?2"+
            "AND r.start_date <= ?3", 
            nativeQuery = true)
    Integer countBusyWorkersByOfficeAndDataRange(Office office, Date startDate, Date endDate);
}
