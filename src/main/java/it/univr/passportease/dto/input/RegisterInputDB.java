package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Class used to store the data of a user to be registered in the database.
 * Contains the data of the user, the hash of the password, the refresh token and the active status.
 */
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