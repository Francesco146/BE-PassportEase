package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.input.RegisterInputDB;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.User;
import it.univr.passportease.helper.map.MapUser;
import it.univr.passportease.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository userRepository;
    private final MapUser mapUser;

    @Override
    public String login(String fiscalCode, String password) {
        return null;
    }

    @Override
    public LoginOutput register(RegisterInput registerInput) {
        CharSequence rawHashPassword = new BCryptPasswordEncoder().encode(registerInput.getPassword());

        RegisterInputDB registerInputDB = new RegisterInputDB(
                registerInput,
                String.valueOf(rawHashPassword),
                true,
                "refreshToken"
        );
        User user = mapUser.mapRegisterInputDBToUser(registerInputDB);
        User addedUser = userRepository.save(user);

        return mapUser.mapUserToLoginOutput(
                addedUser,
                "accessToken"
        );
    }
}
