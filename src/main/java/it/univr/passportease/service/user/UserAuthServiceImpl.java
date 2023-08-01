package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.input.RegisterInputDB;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.helper.map.MapUser;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.repository.WorkerRepository;
import it.univr.passportease.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Autowired
    private HttpServletRequest request;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final MapUser mapUser;
    private final JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public LoginOutput login(String fiscalCode, String password) {
        // get user id
        Optional<User> _user = userRepository.findByFiscalCode(fiscalCode);
        _user.orElseThrow(() -> new RuntimeException("User not found"));
        User user = _user.get();

        // TODO: invalidate old refresh token

        String id = user.getId().toString();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(id, password));

        if (authentication.isAuthenticated()) {
            String accessToken = jwtService.generateAccessToken(UUID.fromString(id));
            String refreshToken = jwtService.generateRefreshToken(UUID.fromString(id));

            // save refresh token
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            return mapUser.mapUserToLoginOutput(
                    user,
                    accessToken);
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @Override
    public LoginOutput register(RegisterInput registerInput) {
        // check if user already exists
        Optional<User> userDB = userRepository.findByFiscalCode(registerInput.getFiscalCode());
        if (userDB.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // TODO: check if user exists in the citizens table

        // create user
        RegisterInputDB registerInputDB = new RegisterInputDB(
                registerInput,
                passwordEncoder.encode(registerInput.getPassword()),
                true,
                "");
        User user = mapUser.mapRegisterInputDBToUser(registerInputDB);
        User addedUser = userRepository.save(user);

        // generate access token and refresh token
        String accessToken = jwtService.generateAccessToken(addedUser.getId());
        String refreshToken = jwtService.generateRefreshToken(addedUser.getId());

        // save refresh token
        addedUser.setRefreshToken(refreshToken);
        userRepository.save(addedUser);

        // return login output
        return mapUser.mapUserToLoginOutput(
                addedUser,
                accessToken);

    }

    @Override
    // add filter to check if access token is valid
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER')")
    public void logout() {
        String authorizationHeader = request.getHeader("Authorization");
        // remove access token from cache
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new RuntimeException("Invalid token");

        String accessToken = authorizationHeader.substring(7);
        UUID userId = jwtService.extractId(accessToken);
        Optional<User> _user = userRepository.findById(userId);
        Optional<Worker> _worker = workerRepository.findById(userId);
        if (_user.isPresent()) {
            _user.get().setRefreshToken("");
            userRepository.save(_user.get());
        } else if (_worker.isPresent()) {
            _worker.get().setRefreshToken("");
            workerRepository.save(_worker.get());
        } else
            throw new RuntimeException("Invalid token");

        jwtService.invalidateAccessToken(accessToken);
        // invalidate refresh token
    }
}
