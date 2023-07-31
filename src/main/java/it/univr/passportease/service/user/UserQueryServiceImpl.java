package it.univr.passportease.service.user;

import it.univr.passportease.dto.output.UserOutput;
import it.univr.passportease.entity.User;
import it.univr.passportease.helper.map.MapUser;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.service.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final MapUser mapUser;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public UserOutput getUserDetails(@RequestHeader("Authorization") String token) {
        UUID id = jwtService.extractId(token);

        Optional<User> _user = userRepository.findById(id);
        _user.orElseThrow(() -> new RuntimeException("User not found"));
        User user = _user.get();

        return mapUser.mapUserToUserOutput(user);
    }


}
