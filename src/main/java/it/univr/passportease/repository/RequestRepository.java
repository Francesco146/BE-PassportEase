package it.univr.passportease.repository;


import it.univr.passportease.entity.Request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

// TODO: add startTime and endTime to the query

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
    @Query(value = "SELECT COUNT(*) " +
            "FROM requests r, requests_offices ro, offices o " +
            "WHERE r.id = ro.request_id " +
            "AND ro.office_id = o.id " +
            "AND ro.office_id = :officeId " +
            "AND r.start_date >= :startDate " +
            "AND r.start_date <= :endDate", nativeQuery = true)
    long countBusyWorkers(@Param("officeId") UUID officeId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
