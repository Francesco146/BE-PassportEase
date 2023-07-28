package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.LoginOutput;

public interface UserAuthService {
    String login(String fiscalCode, String password);

    LoginOutput register(RegisterInput registerInput);
}
