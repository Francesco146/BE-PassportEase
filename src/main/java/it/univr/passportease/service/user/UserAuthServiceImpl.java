package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.input.RegisterInputDB;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.User;
import it.univr.passportease.helper.map.MapUser;
import it.univr.passportease.repository.CitizenRepository;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.service.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository userRepository;
    private final CitizenRepository citizenRepository;
    private final MapUser mapUser;
    private final JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public LoginOutput login(String fiscalCode, String password) {
        // get user
        Optional<User> _user = userRepository.findByFiscalCode(fiscalCode);
        _user.orElseThrow(() -> new RuntimeException("User not found"));

        User user = _user.get();
        String userId = user.getId().toString();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userId, password)
        );

        if (!authentication.isAuthenticated()) throw new UsernameNotFoundException("invalid user request !");

        user.setRefreshToken(
                jwtService.generateRefreshToken(UUID.fromString(userId))
        );
        userRepository.save(user);

        return mapUser.mapUserToLoginOutput(
                user,
                jwtService.generateAccessToken(UUID.fromString(userId))
        );
    }

    @Override
    public LoginOutput register(RegisterInput registerInput) {
        String fiscalCode = registerInput.getFiscalCode();

        Optional<User> userDB = userRepository.findByFiscalCode(fiscalCode);
        if (userDB.isPresent()) throw new RuntimeException("User already exists");

        if (citizenRepository.findByFiscalCode(fiscalCode).isEmpty())
            throw new RuntimeException("No citizen found with this fiscal code");

        User _user = mapUser.mapRegisterInputDBToUser(
                new RegisterInputDB(
                        registerInput,
                        passwordEncoder.encode(registerInput.getPassword()),
                        true,
                        ""
                )
        );
        User addedUser = userRepository.save(_user);

        // set refresh token after saving user because we need the user id
        addedUser.setRefreshToken(jwtService.generateRefreshToken(addedUser.getId()));
        userRepository.save(addedUser);

        return mapUser.mapUserToLoginOutput(
                addedUser,
                jwtService.generateAccessToken(addedUser.getId())
        );
    }
}
