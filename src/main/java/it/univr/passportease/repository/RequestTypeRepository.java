package it.univr.passportease.repository;

import it.univr.passportease.entity.RequestType;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestTypeRepository extends JpaRepository<RequestType, UUID> {

}
