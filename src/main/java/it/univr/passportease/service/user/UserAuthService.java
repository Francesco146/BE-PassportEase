package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.exception.WrongPasswordException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserAuthService {
    LoginOutput login(String fiscalCode, String password) throws UsernameNotFoundException, WrongPasswordException;

    LoginOutput register(RegisterInput registerInput);
}
