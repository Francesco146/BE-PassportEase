package it.univr.passportease.service;

import it.univr.passportease.dto.output.JWTSet;

public interface UserWorkerMutationService {
    void logout();

    JWTSet refreshAccessToken(String token, String refreshToken);

    void changePassword(String oldPassword, String newPassword);

    String changeEmail(String newEmail, String oldEmail);
}
