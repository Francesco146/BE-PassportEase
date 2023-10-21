package it.univr.passportease.service.worker.impl;

import it.univr.passportease.dto.input.WorkerInput;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.exception.notfound.OfficeNotFoundException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.exception.security.WrongPasswordException;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.helper.map.MapWorker;
import it.univr.passportease.repository.OfficeRepository;
import it.univr.passportease.repository.WorkerRepository;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.worker.WorkerAuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link WorkerAuthService}, provides methods for authentication of {@link Worker}
 */
@Service
@AllArgsConstructor
public class WorkerAuthServiceImpl implements WorkerAuthService {

    /**
     * Repository for {@link Worker} entity
     */
    private final WorkerRepository workerRepository;
    /**
     * Repository for {@link Office} entity
     */
    private final OfficeRepository officeRepository;

    /**
     * Service for mapping {@link Worker} to {@link LoginOutput}
     */
    private final MapWorker mapWorker;

    /**
     * Service for generating {@link JWT}
     */
    private final JwtService jwtService;

    /**
     * Service for authenticating {@link Worker}
     */
    private AuthenticationManager authenticationManager;
    /**
     * Service for encoding password
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Login a {@link Worker}
     *
     * @param username username of {@link Worker}
     * @param password password of {@link Worker}
     * @return {@link LoginOutput} with {@link JWTSet} of {@link Worker}
     * @throws WrongPasswordException  if password is wrong
     * @throws WorkerNotFoundException if {@link Worker} is not found
     */
    @Override
    public LoginOutput login(String username, String password) throws WrongPasswordException, WorkerNotFoundException {
        Optional<Worker> optionalWorker = workerRepository.findByUsername(username);
        if (optionalWorker.isEmpty()) throw new WorkerNotFoundException("Worker not found");

        Worker worker = optionalWorker.get();

        String id = worker.getId().toString();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(id, password));

        if (!authentication.isAuthenticated())
            throw new WrongPasswordException("Invalid credentials");

        worker.setRefreshToken(jwtService.generateRefreshToken(UUID.fromString(id)).getToken());
        workerRepository.save(worker);

        return mapWorker.mapWorkerToLoginOutput(
                worker,
                jwtService.generateAccessToken(UUID.fromString(id))
        );
    }

    /**
     * Register a {@link Worker}
     *
     * @param workerInput {@link WorkerInput} with data of {@link Worker}
     * @return {@link LoginOutput} with {@link JWTSet} of {@link Worker}
     * @throws OfficeNotFoundException if {@link Office} is not found
     * @deprecated Refreshes {@link JWT} of {@link Worker}, it's deprecated because the {@link Worker} is inserted in the database
     * manually by the administrator
     */
    @Override
    @Deprecated(since = "0.0.1")
    public LoginOutput register(WorkerInput workerInput) throws OfficeNotFoundException {

        Optional<Office> office = officeRepository.findByName(workerInput.getOfficeName());
        if (office.isEmpty()) throw new OfficeNotFoundException("Office not found");

        Worker worker = new Worker();
        worker.setHashPassword(passwordEncoder.encode(workerInput.getPassword()));
        worker.setCreatedAt(new Date());
        worker.setUpdatedAt(new Date());
        worker.setUsername(workerInput.getUsername());
        worker.setEmail(workerInput.getEmail());
        worker.setRefreshToken("");
        worker.setOffice(office.get());

        Worker addedWorker = workerRepository.save(worker);
        JWT refreshToken = jwtService.generateRefreshToken(addedWorker.getId());

        addedWorker.setRefreshToken(refreshToken.getToken());
        workerRepository.save(addedWorker);

        JWT accessToken = jwtService.generateAccessToken(addedWorker.getId());

        return new LoginOutput(
                addedWorker.getId(),
                new JWTSet(
                        accessToken.getToken(),
                        refreshToken.getToken()
                )
        );
    }
}
