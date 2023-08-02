package it.univr.passportease.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univr.passportease.entity.Availability;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {

}
