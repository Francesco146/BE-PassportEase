package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;

public interface UserAuthService {
    LoginOutput login(String fiscalCode, String password);

    LoginOutput register(RegisterInput registerInput);

    void logout();

    JWTSet refreshAccessToken(String token, String refreshToken);

    void changePassword(String oldPassword, String newPassword);

    String changeEmail(String newEmail, String oldEmail);
}
