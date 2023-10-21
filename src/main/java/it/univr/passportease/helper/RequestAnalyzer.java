package it.univr.passportease.helper;

import it.univr.passportease.exception.security.AuthenticationCredentialsNotFoundException;
import it.univr.passportease.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is used to analyze the request and extract the JWT token from the Authorization header.
 * It also checks if the token is valid and not expired.
 */
@Service
public class RequestAnalyzer {

    /**
     * The regex used to check if the token is valid.
     */
    private static final String JWT_REGEX = "^[\\w-]*\\.[\\w-]*\\.[\\w-]*$";
    /**
     * The request to analyze.
     */
    private final HttpServletRequest request;
    /**
     * The service used to check if the token is valid and not expired.
     */
    private final JwtService jwtService;

    /**
     * Constructor for the RequestAnalyzer class.
     *
     * @param jwtService The service used to check if the token is valid and not expired
     * @param request    The request to analyze
     */
    @Autowired
    RequestAnalyzer(JwtService jwtService, HttpServletRequest request) {
        this.jwtService = jwtService;
        this.request = request;
    }


    /**
     * This method extracts the JWT token from the Authorization header of the request.
     *
     * @return The JWT token extracted from the Authorization header
     * @throws AuthenticationCredentialsNotFoundException If the token is not provided, is invalid or expired
     */
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
