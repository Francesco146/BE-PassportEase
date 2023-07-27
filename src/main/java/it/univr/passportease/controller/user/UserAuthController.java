package it.univr.passportease.controller.user;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserAuthController {
    
    @MutationMapping
    public LoginOutput loginUser(@Argument("fiscalCode") String fiscalCode, @Argument("password") String password) {
        System.out.println(fiscalCode);
        LoginOutput loginOutput = new LoginOutput("12345", new JWTSet("jwt", "refreshJwt"));
        return loginOutput;
    }
}
