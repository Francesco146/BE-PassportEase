package it.univr.passportease.controller.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.JWTSet;
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
        LoginOutput login = userAuthService.login(fiscalCode, password);
        redisTemplate.opsForValue().set(
                login.getId().toString(),
                login.getJwtSet().getAccessToken(),
                15,
                java.util.concurrent.TimeUnit.MINUTES
        );
        return login;
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public void logoutUser() {
        userAuthService.logout();
    }

    @MutationMapping
    public LoginOutput registerUser(@Argument("registerInput") RegisterInput registerInput) {
        LoginOutput register = userAuthService.register(registerInput);
        redisTemplate.opsForValue().set(
                register.getId().toString(),
                register.getJwtSet().getAccessToken(),
                15,
                java.util.concurrent.TimeUnit.MINUTES
        );
        return register;
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public JWTSet refreshAccessTokenUser(@Argument("refreshToken") String refreshToken) {
        String token = request.getHeader("Authorization").substring(7);
        return userAuthService.refreshAccessToken(token, refreshToken);
    }

    @MutationMapping
    public void changePasswordUser(@Argument("oldPassword") String oldPassword, @Argument("newPassword") String newPassword) {
        userAuthService.changePassword(oldPassword, newPassword);
    }

    @MutationMapping
    public String changeEmailUser(@Argument("newEmail") String newEmail, @Argument("oldEmail") String oldEmail) {
        return userAuthService.changeEmail(newEmail, oldEmail);
    }
}
