package it.univr.passportease.helper.map;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.input.RegisterInputDB;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@AllArgsConstructor
@Component
public class MapUser {
    public User mapRegisterInputDBToUser(RegisterInputDB registerInputDB) {
        RegisterInput registerInput = registerInputDB.getRegisterInput();
        User user = new User();
        user.setFiscalCode(registerInput.getFiscalCode());
        user.setEmail(registerInput.getEmail());
        user.setName(registerInput.getName());
        user.setSurname(registerInput.getSurname());
        user.setCityOfBirth(registerInput.getCityOfBirth());
        user.setDateOfBirth(registerInput.getDateOfBirth());
        user.setHashPassword(registerInputDB.getHashPassword());
        user.setActive(registerInputDB.getActive());
        user.setRefreshToken(registerInputDB.getRefreshToken());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return user;
    }

    public LoginOutput mapUserToLoginOutput(User user, String accessToken) {
        return new LoginOutput(
                user.getId(),
                new JWTSet(
                        accessToken,
                        user.getRefreshToken()));
    }
}
