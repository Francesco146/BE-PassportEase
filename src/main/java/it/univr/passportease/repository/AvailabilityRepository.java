package it.univr.passportease.repository;

import it.univr.passportease.entity.Availability;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID>, JpaSpecificationExecutor<Availability> {
    @NotNull
    List<Availability> findAll(@NotNull Specification<Availability> specification);

    List<Availability> findByRequestId(UUID requestId);
}
