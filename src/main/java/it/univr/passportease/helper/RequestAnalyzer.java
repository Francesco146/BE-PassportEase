package it.univr.passportease.helper;

import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestAnalyzer {

    private static final String JWT_REGEX = "^[\\w-]*\\.[\\w-]*\\.[\\w-]*$";
    private final HttpServletRequest request;
    private final JwtService jwtService;

    @Autowired
    RequestAnalyzer(JwtService jwtService, HttpServletRequest request) {
        this.jwtService = jwtService;
        this.request = request;
    }


    public JWT getTokenFromRequest() throws AuthenticationCredentialsNotFoundException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null)
            throw new AuthenticationCredentialsNotFoundException("No token provided");

        if (!authorizationHeader.substring(7).matches(JWT_REGEX) || !authorizationHeader.startsWith("Bearer "))
            throw new AuthenticationCredentialsNotFoundException("Invalid token provided");

        if (Boolean.TRUE.equals(jwtService.isTokenExpired(new JWT(authorizationHeader.substring(7)))))
            throw new AuthenticationCredentialsNotFoundException("Expired token provided");

        return new JWT(authorizationHeader.substring(7));
    }
}
