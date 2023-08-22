package it.univr.passportease.service.user.impl;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.repository.NotificationRepository;
import it.univr.passportease.repository.ReservationRepository;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.user.UserQueryService;
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
    private final ReservationRepository reservationRepository;
    private final JwtService jwtService;

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public User getUserDetails(String token) throws UserNotFoundException {
        UUID id = jwtService.extractId(token);
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) throw new UserNotFoundException("User not found");

        return user.get();
    }

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public List<Notification> getUserNotifications(String token) {
        UUID id = jwtService.extractId(token);
        return notificationRepository.findByUserId(id);
    }

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public List<Availability> getUserReservations(String token) {
        UUID id = jwtService.extractId(token);
        return reservationRepository.findByUserId(id);
    }

}
