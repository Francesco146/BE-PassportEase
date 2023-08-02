package it.univr.passportease.service;

import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.repository.WorkerRepository;
import it.univr.passportease.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private HttpServletRequest request;

    private PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public void logout() {
        String accessToken = getTokenFromRequest();
        jwtService.invalidateRefreshToken(accessToken);
        if (!jwtService.invalidateAccessToken(accessToken))
            throw new RuntimeException("Error while invalidating access token");
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public JWTSet refreshAccessToken(String token, String refreshToken) {
        Object userOrWorker = jwtService.getUserOrWorkerFromToken(token);
        if (userOrWorker == null)
            throw new RuntimeException("Invalid access token");

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
    public void changePassword(String oldPassword, String newPassword) throws RuntimeException {
        String accessToken = getTokenFromRequest();

        Object userOrWorker = jwtService.getUserOrWorkerFromToken(accessToken);
        if (userOrWorker == null)
            throw new RuntimeException("Invalid access token");


        if (userOrWorker instanceof User user) {
            if (!passwordEncoder.matches(oldPassword, user.getHashPassword()))
                throw new RuntimeException("Invalid old password");
            user.setHashPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

        } else if (userOrWorker instanceof Worker worker) {
            if (!passwordEncoder.matches(oldPassword, worker.getHashPassword()))
                throw new RuntimeException("Invalid old password");

            worker.setHashPassword(passwordEncoder.encode(newPassword));
            workerRepository.save(worker);

        } else
            throw new RuntimeException("Unknown error: cannot change password because of token is not from user or worker");
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'WORKER') && hasAuthority('VALIDATED')")
    public String changeEmail(String newEmail, String oldEmail) {
        String accessToken = getTokenFromRequest();

        if (!emailValid(newEmail))
            throw new RuntimeException("Invalid email");

        Object userOrWorker = jwtService.getUserOrWorkerFromToken(accessToken);
        if (userOrWorker == null)
            throw new RuntimeException("Invalid access token");

        if (userOrWorker instanceof User user) {
            if (!user.getEmail().equals(oldEmail))
                throw new RuntimeException("Invalid old email");
            user.setEmail(newEmail);
            userRepository.save(user);
        } else if (userOrWorker instanceof Worker worker) {
            if (!worker.getEmail().equals(oldEmail))
                throw new RuntimeException("Invalid old email");
            worker.setEmail(newEmail);
            workerRepository.save(worker);
        } else
            throw new RuntimeException("Unknown error: cannot change email because of token is not from user or worker");
        return newEmail;
    }

    private boolean emailValid(String email) {
        return Pattern
                .compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
                .matcher(email)
                .matches();
    }

    private String getTokenFromRequest() throws RuntimeException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new RuntimeException("Invalid token");
        return authorizationHeader.substring(7);
    }
}
