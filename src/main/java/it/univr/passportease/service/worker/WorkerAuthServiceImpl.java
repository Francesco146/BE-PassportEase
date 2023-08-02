package it.univr.passportease.service.worker;

import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.helper.map.MapWorker;
import it.univr.passportease.repository.WorkerRepository;
import it.univr.passportease.service.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WorkerAuthServiceImpl implements WorkerAuthService {
    private final WorkerRepository workerRepository;
    private final MapWorker mapWorker;
    private final JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public LoginOutput login(String username, String password) {
        Optional<Worker> worker = workerRepository.findByUsername(username);
        if (worker.isEmpty()) throw new RuntimeException("Worker not found");

        Worker _worker = worker.get();

        String id = _worker.getId().toString();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(id, password));

        if (!authentication.isAuthenticated())
            throw new RuntimeException("Invalid worker request !");

        _worker.setRefreshToken(jwtService.generateRefreshToken(UUID.fromString(id)));
        workerRepository.save(_worker);

        return mapWorker.mapWorkerToLoginOutput(
                _worker,
                jwtService.generateAccessToken(UUID.fromString(id))
        );
    }
}
