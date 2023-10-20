package it.univr.passportease.helper.map;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.input.RegisterInputDB;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.User;
import it.univr.passportease.helper.JWT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Map the {@link RegisterInputDB} to {@link User} and {@link JWT} to {@link LoginOutput}.
 */
@AllArgsConstructor
@Component
public class MapUser {
    /**
     * @param registerInputDB {@link RegisterInputDB} to map.
     * @return {@link User} mapped.
     */
    public User mapRegisterInputDBToUser(RegisterInputDB registerInputDB) {
        RegisterInput registerInput = registerInputDB.getRegisterInput();
        User user = new User();
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setFiscalCode(registerInput.getFiscalCode());
        user.setEmail(registerInput.getEmail());
        user.setName(registerInput.getName());
        user.setSurname(registerInput.getSurname());
        user.setCityOfBirth(registerInput.getCityOfBirth());
        user.setDateOfBirth(registerInput.getDateOfBirth());
        user.setHashPassword(registerInputDB.getHashPassword());
        user.setActive(registerInputDB.getActive());
        user.setRefreshToken(registerInputDB.getRefreshToken());
        return user;
    }

    /**
     * @param user        {@link User} to map.
     * @param accessToken {@link JWT} to map.
     * @return {@link LoginOutput} mapped.
     */
    public LoginOutput mapUserToLoginOutput(User user, JWT accessToken) {
        return new LoginOutput(
                user.getId(),
                new JWTSet(
                        accessToken.getToken(),
                        user.getRefreshToken())
        );
    }
}
