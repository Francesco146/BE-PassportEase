package it.univr.passportease.config;

import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public AppUserDetailsService(UserRepository userRepository, WorkerRepository workerRepository, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.workerRepository = workerRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(id));
        Optional<Worker> optionalWorker = workerRepository.findById(UUID.fromString(id));
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (optionalUser.isPresent()) {
            authorities.add(new SimpleGrantedAuthority("USER"));
            if (redisTemplate.opsForValue().get(id) != null) authorities.add(new SimpleGrantedAuthority("VALIDATED"));

            User user = optionalUser.get();
            return new AppUserDetails(user.getId(), user.getHashPassword(), authorities);
        } else if (optionalWorker.isPresent()) {
            authorities.add(new SimpleGrantedAuthority("WORKER"));
            Worker worker = optionalWorker.get();
            if (redisTemplate.opsForValue().get(id) != null) authorities.add(new SimpleGrantedAuthority("VALIDATED"));

            return new AppUserDetails(worker.getId(), worker.getHashPassword(), authorities);
        } else throw new UsernameNotFoundException("User not found");
    }
}
