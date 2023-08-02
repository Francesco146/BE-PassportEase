package it.univr.passportease.repository;

import it.univr.passportease.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByFiscalCode(String fiscalCode);

    @NotNull
    Optional<User> findById(@NotNull UUID id);
}
