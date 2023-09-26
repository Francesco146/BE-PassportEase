package it.univr.passportease.service.worker.impl;

import it.univr.passportease.dto.input.WorkerInput;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.exception.notfound.OfficeNotFoundException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.exception.security.WrongPasswordException;
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

@Service
@AllArgsConstructor
public class WorkerAuthServiceImpl implements WorkerAuthService {
    private final WorkerRepository workerRepository;
    private final MapWorker mapWorker;
    private final JwtService jwtService;
    private final OfficeRepository officeRepository;

    private AuthenticationManager authenticationManager;

    private PasswordEncoder passwordEncoder;

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

        worker.setRefreshToken(jwtService.generateRefreshToken(UUID.fromString(id)));
        workerRepository.save(worker);

        return mapWorker.mapWorkerToLoginOutput(
                worker,
                jwtService.generateAccessToken(UUID.fromString(id))
        );
    }

    @Override
    public LoginOutput register(WorkerInput workerInput) throws OfficeNotFoundException {
        // TODO: deprecated

        Optional<Office> office = officeRepository.findByName(workerInput.getOfficeName());
        if (office.isEmpty()) throw new OfficeNotFoundException("Office not found");

        Worker worker = new Worker();
        worker.setUsername(workerInput.getUsername());
        worker.setEmail(workerInput.getEmail());
        worker.setHashPassword(passwordEncoder.encode(workerInput.getPassword()));
        worker.setRefreshToken("");
        worker.setOffice(office.get());
        worker.setCreatedAt(new Date());
        worker.setUpdatedAt(new Date());

        Worker addedWorker = workerRepository.save(worker);
        String refreshToken = jwtService.generateRefreshToken(addedWorker.getId());

        addedWorker.setRefreshToken(refreshToken);
        workerRepository.save(addedWorker);

        String accessToken = jwtService.generateAccessToken(addedWorker.getId());

        return new LoginOutput(
                addedWorker.getId(),
                new JWTSet(
                        accessToken,
                        refreshToken
                )
        );
    }
}
