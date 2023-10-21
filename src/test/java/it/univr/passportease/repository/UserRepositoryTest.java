package it.univr.passportease.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

/**
 * This class tests the {@link UserRepository} class.
 */
@ActiveProfiles("test")
@EnableJpaRepositories
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    /**
     * This method tests the ability to find a user by its fiscal code.
     */
    @Test
    void createUser() {
        Assertions.assertNotNull(userRepository.findByFiscalCode("BLSCLL96D55E463O").orElse(null));
        System.out.println("[!] User found: " + userRepository.findByFiscalCode("BLSCLL96D55E463O") + "\n");
    }

}