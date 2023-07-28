package it.univr.passportease.controller.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.service.user.UserAuthService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;

    @MutationMapping
    public LoginOutput loginUser(@Argument("fiscalCode") String fiscalCode, @Argument("password") String password) {
        return null;
    }

    // registerUser(registerInput: RegisterInput): RegisterUserOutput!

    @MutationMapping
    public LoginOutput registerUser(@Argument("registerInput") RegisterInput registerInput) {
        return userAuthService.register(registerInput);
    }

}
