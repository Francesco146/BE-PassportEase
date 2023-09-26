package it.univr.passportease.helper;

import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestAnalyzer {
    private final HttpServletRequest request;

    private JwtService jwtService;

    @Autowired
    RequestAnalyzer(JwtService jwtService) {
        this.jwtService = jwtService;
        this.request = null;
    }

    public RequestAnalyzer(HttpServletRequest request) {
        this.request = request;
    }

    public String getTokenFromRequest() throws AuthenticationCredentialsNotFoundException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ") || jwtService.isTokenExpired(authorizationHeader.substring(7)))
            throw new AuthenticationCredentialsNotFoundException("Invalid token or no token provided");
        return authorizationHeader.substring(7);
    }
}
