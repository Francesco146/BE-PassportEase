package it.univr.passportease.helper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RequestAnalyzer {
    private final HttpServletRequest request;

    public RequestAnalyzer(HttpServletRequest request) {
        this.request = request;
    }

    public String getTokenFromRequest() throws AuthenticationCredentialsNotFoundException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new AuthenticationCredentialsNotFoundException("Invalid token or no token provided");
        return authorizationHeader.substring(7);
    }
}
