package it.univr.passportease.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import it.univr.passportease.entity.RequestOffice;

public interface RequestOfficeRepository extends JpaRepository<RequestOffice, UUID> {
    List<RequestOffice> findByRequestId(UUID requestId);
}
