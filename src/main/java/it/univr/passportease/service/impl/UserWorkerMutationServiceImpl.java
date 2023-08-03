package it.univr.passportease.service.impl;

import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.exception.invalid.InvalidEmailException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.exception.security.TokenNotInRedisException;
import it.univr.passportease.exception.security.WrongPasswordException;
import it.univr.passportease.helper.RequestAnalyzer;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.repository.WorkerRepository;
import it.univr.passportease.service.UserWorkerMutationService;
import it.univr.passportease.service.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserWorkerMutationServiceImpl implements UserWorkerMutationService {
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    private final JwtService jwtService;
    private RequestAnalyzer requestAnalyzer;

    private PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public void logout() throws UserNotFoundException, TokenNotInRedisException, AuthenticationCredentialsNotFoundException {
        String accessToken = requestAnalyzer.getTokenFromRequest();
        jwtService.invalidateRefreshToken(accessToken);
        if (!jwtService.invalidateAccessToken(accessToken))
            throw new TokenNotInRedisException("Error while invalidating access token");
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public JWTSet refreshAccessToken(String token, String refreshToken) throws UserNotFoundException {
        Object userOrWorker = jwtService.getUserOrWorkerFromToken(token);

        UUID id = jwtService.extractId(token);
        String newRefreshToken = jwtService.generateRefreshToken(id);

        if (userOrWorker instanceof User user) {
            if (!user.getRefreshToken().equals(refreshToken))
                throw new RuntimeException("Invalid refresh token");
            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);
        } else if (userOrWorker instanceof Worker worker) {
            if (!worker.getRefreshToken().equals(refreshToken))
                throw new RuntimeException("Invalid refresh token");
            worker.setRefreshToken(newRefreshToken);
            workerRepository.save(worker);
        } else
            throw new RuntimeException("Invalid refresh token");

        return new JWTSet(jwtService.generateAccessToken(id), newRefreshToken);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public void changePassword(String oldPassword, String newPassword)
            throws UserNotFoundException, WrongPasswordException, AuthenticationCredentialsNotFoundException {
        String accessToken = requestAnalyzer.getTokenFromRequest();

        Object userOrWorker = jwtService.getUserOrWorkerFromToken(accessToken);

        if (userOrWorker instanceof User user) {
            if (!passwordEncoder.matches(oldPassword, user.getHashPassword()))
                throw new WrongPasswordException("Invalid old password");
            user.setHashPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

        } else if (userOrWorker instanceof Worker worker) {
            if (!passwordEncoder.matches(oldPassword, worker.getHashPassword()))
                throw new WrongPasswordException("Invalid old password");
            worker.setHashPassword(passwordEncoder.encode(newPassword));
            workerRepository.save(worker);
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public String changeEmail(String newEmail, String oldEmail)
            throws UserNotFoundException, InvalidEmailException, AuthenticationCredentialsNotFoundException {
        String accessToken = requestAnalyzer.getTokenFromRequest();

        if (!emailValid(newEmail))
            throw new InvalidEmailException("Invalid email");

        Object userOrWorker = jwtService.getUserOrWorkerFromToken(accessToken);

        if (userOrWorker instanceof User user) {
            if (!user.getEmail().equals(oldEmail))
                throw new InvalidEmailException("Invalid old email");
            user.setEmail(newEmail);
            userRepository.save(user);
        } else if (userOrWorker instanceof Worker worker) {
            if (!worker.getEmail().equals(oldEmail))
                throw new InvalidEmailException("Invalid old email");
            worker.setEmail(newEmail);
            workerRepository.save(worker);
        }
        return newEmail;
    }

    private boolean emailValid(String email) {
        return Pattern
                .compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
                .matcher(email)
                .matches();
    }
}
