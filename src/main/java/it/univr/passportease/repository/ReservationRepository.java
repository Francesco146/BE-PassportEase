package it.univr.passportease.repository;

import it.univr.passportease.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Availability, UUID> {

    List<Availability> findByUserId(UUID id);
}
