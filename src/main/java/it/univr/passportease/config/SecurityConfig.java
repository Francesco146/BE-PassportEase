package it.univr.passportease.config;

import it.univr.passportease.filter.JwtAuthFilter;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.repository.WorkerRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private UserRepository userRepository;
    private WorkerRepository workerRepository;
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @return UserDetailsService, used to retrieve user details from the database or from the Redis cache
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new AppUserDetailsService(userRepository, workerRepository, redisTemplate);
    }

    /**
     * @param http       HttpSecurity object
     * @param authFilter JwtAuthFilter object
     * @return SecurityFilterChain, used to configure the security filter chain. It is used to configure the
     * authentication method, the authorization method and the session management.
     * @throws Exception if an error occurs in the csrf configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter authFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers(request ->
                        request.getServletPath().startsWith("/graphql") ||
                                request.getServletPath().startsWith("/actuator/health")
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(request ->
                                request.getServletPath().startsWith("/actuator/health") ||
                                        request.getServletPath().startsWith("/graphql")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .maximumSessions(1)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * @return PasswordEncoder, used to encode the password
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @return AuthenticationProvider, used to authenticate the user
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * @param config AuthenticationConfiguration object, used to configure the authentication manager
     * @return AuthenticationManager, used to authenticate the user
     * @throws Exception if an error occurs in the authentication manager configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
