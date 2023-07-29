package it.univr.passportease.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class HashHelper {
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Boolean checkHash(CharSequence password, String hashPassword) {
        return passwordEncoder.matches(password, hashPassword);
    }
}
