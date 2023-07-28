package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class RegisterInputDB {
    private RegisterInput registerInput;
    private String hashPassword;
    private Boolean active;
    private String refreshToken;
}