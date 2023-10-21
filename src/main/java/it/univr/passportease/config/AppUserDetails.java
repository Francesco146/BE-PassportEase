package it.univr.passportease.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link UserDetails} that represents a user
 */
public class AppUserDetails implements UserDetails {

    /**
     * The user id
     */
    private final String name;
    /**
     * The user password
     */
    @lombok.Getter
    private final String password;
    /**
     * The authorities of the user
     */
    @lombok.Getter
    private final List<GrantedAuthority> authorities;

    /**
     * Constructor of {@link AppUserDetails}. It takes the user id, the user password and the user authorities
     * and creates a {@link UserDetails} object.
     *
     * @param id           user id
     * @param hashPassword user password
     * @param authorities  user authorities
     */
    public AppUserDetails(UUID id, String hashPassword, List<GrantedAuthority> authorities) {
        name = id.toString();
        password = hashPassword;
        this.authorities = List.copyOf(authorities);
    }

    /**
     * @return user id
     */
    @Override
    public String getUsername() {
        return name;
    }

    /**
     * @return true if the user is not expired, always true in this implementation
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * @return true if the user is not locked, always true in this implementation
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * @return true if the user credentials are not expired, always true in this implementation
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * @return true if the user is enabled, always true in this implementation
     */
    @Override
    public boolean isEnabled() {
        return true;
    }


}
