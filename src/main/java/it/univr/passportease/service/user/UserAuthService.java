package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.LoginOutput;
import org.springframework.web.bind.annotation.RequestHeader;

public interface UserAuthService {
    LoginOutput login(String fiscalCode, String password);

    LoginOutput register(RegisterInput registerInput);

    void logout(@RequestHeader("Authorization") String authorizationHeader);
}
