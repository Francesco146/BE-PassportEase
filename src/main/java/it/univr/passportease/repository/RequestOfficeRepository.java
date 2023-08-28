package it.univr.passportease.repository;

import it.univr.passportease.entity.RequestOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestOfficeRepository extends JpaRepository<RequestOffice, UUID> {
    List<RequestOffice> findByRequestId(UUID requestId);
}
