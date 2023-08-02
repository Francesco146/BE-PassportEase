package it.univr.passportease.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univr.passportease.entity.RequestOffice;

@Repository
public interface RequestOfficeRepository extends JpaRepository<RequestOffice, UUID> {
    
}
