package it.univr.passportease.dto.output;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginOutput {
    private String id;
    private JWTSet jwtSet;
}
