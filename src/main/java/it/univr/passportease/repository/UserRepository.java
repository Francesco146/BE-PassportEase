package it.univr.passportease.repository;

import it.univr.passportease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByFiscalCode(String fiscalCode);
}
