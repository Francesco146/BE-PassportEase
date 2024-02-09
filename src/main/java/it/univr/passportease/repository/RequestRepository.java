package it.univr.passportease.repository;

import it.univr.passportease.entity.Request;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Repository for the {@link Request} entity.
 */
@Repository
public interface RequestRepository extends JpaRepository<Request, UUID>, JpaSpecificationExecutor<Request> {
    /**
     * Finds all the {@link Request} associated to the given worker id.
     *
     * @param officeId  office id to search for
     * @param startDate start date to search for
     * @param endDate   end date to search for
     * @return a list of {@link Request} objects.
     */
    @Query(value = "SELECT r.id, r.duration, r.start_date, r.end_date, r.start_time, r.end_time, r.worker_id, r.request_type_id, r.created_at, r.updated_at " +
            "FROM requests r, requests_offices ro, offices o " +
            "WHERE r.id = ro.request_id " +
            "AND ro.office_id = o.id " +
            "AND ro.office_id = :officeId " +
            "AND r.start_date >= :startDate " +
            "AND r.start_date <= :endDate", nativeQuery = true)
    List<Request> getOfficeRequests(@Param("officeId") UUID officeId, @Param("startDate") Date startDate,
                                    @Param("endDate") Date endDate);

    @NotNull
    List<Request> findAll(@NotNull Specification<Request> specification);
}
