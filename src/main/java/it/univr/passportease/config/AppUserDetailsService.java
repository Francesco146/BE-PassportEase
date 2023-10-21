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

/**
 * This class is used by spring security to authenticate and authorize users
 */
@Component
public class AppUserDetailsService implements UserDetailsService {

    /**
     * The repository of the users
     */
    private final UserRepository userRepository;
    /**
     * The repository of the workers
     */
    private final WorkerRepository workerRepository;
    /**
     * The redis template used to check if the user is validated
     */
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Constructor of {@link AppUserDetailsService}. It takes the repository of the users, the repository of the workers
     * and the redis template used to check if the user is validated.
     * It creates a {@link UserDetailsService} object.
     *
     * @param userRepository   The repository of the users
     * @param workerRepository The repository of the workers
     * @param redisTemplate    The redis template used to check if the user is validated
     */
    @Autowired
    public AppUserDetailsService(UserRepository userRepository, WorkerRepository workerRepository, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.workerRepository = workerRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Load the user details of the user identified by the id
     *
     * @param id the username identifying the user whose data is required. It is the id of the user
     * @return the user details of the user identified by the id
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(id));
        Optional<Worker> optionalWorker = workerRepository.findById(UUID.fromString(id));

        if (optionalUser.isEmpty() && optionalWorker.isEmpty()) throw new UsernameNotFoundException("User not found");

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            addAuthorities(id, authorities, "USER");
            return new AppUserDetails(user.getId(), user.getHashPassword(), authorities);
        }

        Worker worker = optionalWorker.get();

        addAuthorities(id, authorities, "WORKER");
        return new AppUserDetails(worker.getId(), worker.getHashPassword(), authorities);
    }

    /**
     * Add the authorities to the list of authorities of the user
     *
     * @param id          the id of the user
     * @param authorities the list of authorities of the user
     * @param role        the role of the user, it can be "USER" or "WORKER"
     */
    private void addAuthorities(String id, List<GrantedAuthority> authorities, String role) {
        authorities.add(new SimpleGrantedAuthority(role));
        if (redisTemplate.opsForValue().get(id) != null) authorities.add(new SimpleGrantedAuthority("VALIDATED"));
    }
}
