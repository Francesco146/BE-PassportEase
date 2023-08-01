package it.univr.passportease.service.user;

import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.repository.NotificationRepository;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.service.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final JwtService jwtService;

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public User getUserDetails(String token) {
        UUID id = jwtService.extractId(token);
        Optional<User> user = userRepository.findById(id);
        user.orElseThrow(() -> new RuntimeException("User not found"));
        return user.get();
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public List<Notification> getUserNotifications(String token) {
        UUID id = jwtService.extractId(token);
        return notificationRepository.findByUserId(id);
    }

}
