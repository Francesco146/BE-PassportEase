package it.univr.passportease.filter;

import it.univr.passportease.config.AppUserDetailsService;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts every request and checks if the user is authenticated.
 */
@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * The service that handles the JWT.
     */
    private JwtService jwtService;
    /**
     * The service that handles the user details of the application. Used to manage the sessions.
     */
    private AppUserDetailsService userDetailsService;

    /**
     * Checks if the user is authenticated.
     *
     * @param request     The request to be filtered
     * @param response    The response to be filtered
     * @param filterChain The chain of filters to be applied
     * @throws ServletException If the request cannot be handled
     * @throws IOException      If an input or output error occurs while the filter is handling the request, in the
     *                          filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        JWT token = new JWT();
        String id = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token.setToken(authHeader.substring(7));
            id = jwtService.extractId(token).toString();
        }

        if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(id);
            if (jwtService.validTokenFromUserDetails(token, userDetails).equals(true)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
