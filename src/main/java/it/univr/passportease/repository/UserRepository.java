package it.univr.passportease.repository;

import it.univr.passportease.entity.User;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for the {@link User} entity.
 */
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * @param fiscalCode the fiscal code of the user
     * @return the user with the given fiscal code, as an {@link Optional}
     */
    Optional<User> findByFiscalCode(String fiscalCode);

    /**
     * @param id the id of the user
     * @return the user with the given id, as an {@link Optional}
     */
    @NotNull
    Optional<User> findById(@NotNull UUID id);

    /**
     * @param fiscalCode the fiscal code of the user
     */
    void deleteByFiscalCode(String fiscalCode);

    /**
     * @param id must not be {@literal null}.  The id of the user
     * @return {@literal true} if the user with the given id exists, {@literal false} otherwise
     */
    boolean existsById(@NotNull UUID id);
}
