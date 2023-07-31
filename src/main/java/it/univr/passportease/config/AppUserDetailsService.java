package it.univr.passportease.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.repository.WorkerRepository;

@Component
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(UUID.fromString(id));
        Optional<Worker> worker = workerRepository.findById(UUID.fromString(id));
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.isPresent()) {
            authorities.add(new SimpleGrantedAuthority("USER"));
            User _user = user.get();
            return new AppUserDetails(_user.getId(), _user.getHashPassword(), authorities);
        }
        else if (worker.isPresent()) {
            authorities.add(new SimpleGrantedAuthority("WORKER"));
            Worker _worker = worker.get();
            return new AppUserDetails(_worker.getId(), _worker.getHashPassword(), authorities);
        }
        else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
