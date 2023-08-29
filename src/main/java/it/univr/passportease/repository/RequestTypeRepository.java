package it.univr.passportease.repository;

import it.univr.passportease.entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType, UUID> {
    Optional<RequestType> findByName(String name);

    boolean existsByName(String name);
}
