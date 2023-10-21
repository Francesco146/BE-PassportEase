package it.univr.passportease;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class tests the {@link PassportEaseApplication} class.
 */
@SpringBootTest
class PassportEaseApplicationTests {
    @Autowired
    private PassportEaseApplication passportEaseApplication;

    /**
     * This method tests the ability to load the application context.
     */
    @Test
    void contextLoads() {
        Assertions.assertNotNull(passportEaseApplication);
    }

}
