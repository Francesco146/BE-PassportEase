package it.univr.passportease.repository;

import it.univr.passportease.entity.RequestOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for the {@link RequestOffice} entity.
 */
@Repository
public interface RequestOfficeRepository extends JpaRepository<RequestOffice, UUID> {
    /**
     * Finds all the {@link RequestOffice} associated to the given request id.
     *
     * @param requestId The id of the request
     * @return The list of {@link RequestOffice} associated to the request
     */
    List<RequestOffice> findByRequestId(UUID requestId);
}
