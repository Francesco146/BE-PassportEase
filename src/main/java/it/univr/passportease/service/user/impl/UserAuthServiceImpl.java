package it.univr.passportease.service.user.impl;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.input.RegisterInputDB;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.User;
import it.univr.passportease.exception.illegalstate.UserAlreadyExistsException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.security.WrongPasswordException;
import it.univr.passportease.helper.map.MapUser;
import it.univr.passportease.repository.CitizenRepository;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.user.UserAuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link UserAuthService}, which provides methods for user authentication.
 */
@Service
@AllArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository userRepository;
    private final CitizenRepository citizenRepository;

    private final MapUser mapUser;

    private final JwtService jwtService;

    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    /**
     * @param fiscalCode user fiscal code
     * @param password   user password
     * @return {@link LoginOutput} object containing the access token and the refresh token
     * @throws UserNotFoundException  if the user is not found
     * @throws WrongPasswordException if the password is wrong
     */
    @Override
    public LoginOutput login(String fiscalCode, String password) throws UserNotFoundException, WrongPasswordException {
        // get user
        Optional<User> optionalUser = userRepository.findByFiscalCode(fiscalCode);

        if (optionalUser.isEmpty()) throw new UserNotFoundException("User not found");

        User user = optionalUser.get();
        String userId = user.getId().toString();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userId, password)
        );

        // Exception if authentication fails (wrong password)
        if (!authentication.isAuthenticated()) throw new WrongPasswordException("Authentication failed");

        user.setRefreshToken(
                jwtService.generateRefreshToken(UUID.fromString(userId)).getToken()
        );
        userRepository.save(user);

        return mapUser.mapUserToLoginOutput(
                user,
                jwtService.generateAccessToken(UUID.fromString(userId))
        );
    }

    /**
     * @param registerInput {@link RegisterInput} object containing the user data
     * @return {@link LoginOutput} object containing the access token and the refresh token
     * @throws UserNotFoundException      if the user is not found
     * @throws UserAlreadyExistsException if the user already exists
     */
    @Override
    public LoginOutput register(RegisterInput registerInput) throws UserNotFoundException, UserAlreadyExistsException {
        String fiscalCode = registerInput.getFiscalCode();

        Optional<User> userDB = userRepository.findByFiscalCode(fiscalCode);
        if (userDB.isPresent()) throw new UserAlreadyExistsException("User already exists");

        if (citizenRepository.findByFiscalCode(fiscalCode).isEmpty())
            throw new UserNotFoundException("No citizen found with this fiscal code");

        User user = mapUser.mapRegisterInputDBToUser(
                new RegisterInputDB(
                        registerInput,
                        passwordEncoder.encode(registerInput.getPassword()),
                        true,
                        ""
                )
        );
        User addedUser = userRepository.save(user);

        // set refresh token after saving user because we need the user id
        addedUser.setRefreshToken(jwtService.generateRefreshToken(addedUser.getId()).getToken());
        userRepository.save(addedUser);

        return mapUser.mapUserToLoginOutput(
                addedUser,
                jwtService.generateAccessToken(addedUser.getId())
        );
    }
}
