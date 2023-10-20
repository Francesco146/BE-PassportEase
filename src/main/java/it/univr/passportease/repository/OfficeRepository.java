package it.univr.passportease.repository;

import it.univr.passportease.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Office} entity
 */
@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {
    /**
     * @param name name of the office
     * @return the office with the given name, as an {@link Optional}
     */
    Optional<Office> findByName(String name);

    /**
     * @param names names of the offices
     * @return a list of offices with the given names
     */
    List<Office> findAllByNameIn(List<String> names);

}
