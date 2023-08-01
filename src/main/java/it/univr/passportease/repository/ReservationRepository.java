package it.univr.passportease.repository;

import it.univr.passportease.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Availability, UUID> {

    List<Availability> findByUserId(UUID id);
}
