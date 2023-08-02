package it.univr.passportease.controller.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.service.UserWorkerMutationService;
import it.univr.passportease.service.user.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final UserWorkerMutationService userWorkerMutationService;

    @Autowired
    private HttpServletRequest request;

    @MutationMapping
    public LoginOutput loginUser(@Argument("fiscalCode") String fiscalCode, @Argument("password") String password) {
        return userAuthService.login(fiscalCode, password);
    }

    @MutationMapping
    public void logout() {
        userWorkerMutationService.logout();
    }

    @MutationMapping
    public LoginOutput registerUser(@Argument("registerInput") RegisterInput registerInput) {
        return userAuthService.register(registerInput);
    }

    @MutationMapping
    public JWTSet refreshAccessToken(@Argument("refreshToken") String refreshToken) {
        return userWorkerMutationService.refreshAccessToken(getTokenFromRequest(), refreshToken);
    }

    @MutationMapping
    public void changePassword(@Argument("oldPassword") String oldPassword, @Argument("newPassword") String newPassword) {
        userWorkerMutationService.changePassword(oldPassword, newPassword);
    }

    @MutationMapping
    public String changeEmail(@Argument("newEmail") String newEmail, @Argument("oldEmail") String oldEmail) {
        return userWorkerMutationService.changeEmail(newEmail, oldEmail);
    }

    private String getTokenFromRequest() throws RuntimeException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new RuntimeException("Invalid token");
        return authorizationHeader.substring(7);
    }
}
