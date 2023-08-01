package it.univr.passportease.controller.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.service.user.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserAuthController {
    @Autowired
    private HttpServletRequest request;
    private final UserAuthService userAuthService;
    private RedisTemplate<String, String> redisTemplate;


    @MutationMapping
    public LoginOutput loginUser(@Argument("fiscalCode") String fiscalCode, @Argument("password") String password) {
        System.out.println(request.getHeader("Content-Type"));
        LoginOutput login = userAuthService.login(fiscalCode, password);
        redisTemplate.opsForValue().set(login.getId().toString(), login.getJwtSet().getAccessToken());
        return login;
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public void logoutUser() {
    }

    @MutationMapping
    public LoginOutput registerUser(@Argument("registerInput") RegisterInput registerInput) {
        return userAuthService.register(registerInput);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public void refreshAccessTokenUser() {
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public void changePasswordUser() {
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public void changeEmailUser() {
    }
}
