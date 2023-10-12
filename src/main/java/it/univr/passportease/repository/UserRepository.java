package it.univr.passportease.repository;

import it.univr.passportease.entity.User;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByFiscalCode(String fiscalCode);

    @NotNull
    Optional<User> findById(@NotNull UUID id);

    void deleteByFiscalCode(String fiscalCode);
}
