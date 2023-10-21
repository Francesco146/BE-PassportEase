package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * This class is used to store the data of a register input.
 * It's from the user input.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class RegisterInput {
    /**
     * The fiscal code of the user.
     */
    private String fiscalCode;
    /**
     * The email of the user.
     */
    private String email;
    /**
     * The name of the user.
     */
    private String name;
    /**
     * The surname of the user.
     */
    private String surname;
    /**
     * The city of birth of the user.
     */
    private String cityOfBirth;
    /**
     * The date of birth of the user.
     */
    private Date dateOfBirth;
    /**
     * The password of the user.
     */
    private String password;
}
