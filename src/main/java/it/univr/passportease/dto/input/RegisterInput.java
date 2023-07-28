package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class RegisterInput {
    private String fiscalCode;
    private String email;
    private String name;
    private String surname;
    private String cityOfBirth;
    private Date dateOfBirth;
    private String password;


}
