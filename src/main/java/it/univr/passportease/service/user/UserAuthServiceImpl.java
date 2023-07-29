package it.univr.passportease.service.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.input.RegisterInputDB;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.User;
import it.univr.passportease.helper.HashHelper;
import it.univr.passportease.helper.map.MapUser;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.service.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository userRepository;
    private final MapUser mapUser;
    private final JwtService jwtService;
    private final HashHelper hashHelper;

    @Override
    public LoginOutput login(String fiscalCode, String password) {

        // get user by fiscal code
        User user = userRepository.findByFiscalCode(fiscalCode);

        // check if user exists
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // check if passed password hash is equal to password hash in db 
        String hashPasswordDB = user.getHashPassword();
        System.out.println(hashPasswordDB);
        CharSequence hashPassword = hashHelper.hashPassword(password);
        System.out.println(hashPassword.toString());
        if (!hashHelper.checkHash(hashPassword, hashPasswordDB)) {
            throw new RuntimeException("Password is not correct");
        }

        // generate access token
        String accessToken = jwtService.generateAccessToken(user.getId());

        // generate refresh token
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        // TODO: invalidate old refresh token

        // save refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // return login output
        return mapUser.mapUserToLoginOutput(
                user,
                accessToken
        );
    }

    @Override
    public LoginOutput register(RegisterInput registerInput) {
        // check if user already exists
        User userDB = userRepository.findByFiscalCode(registerInput.getFiscalCode());
        if (userDB != null) {
            throw new RuntimeException("User already exists");
        }

        // create user
        CharSequence hashPassword = hashHelper.hashPassword(registerInput.getPassword());
        RegisterInputDB registerInputDB = new RegisterInputDB(
                registerInput,
                hashPassword.toString(),
                true,
                ""
        );
        User user = mapUser.mapRegisterInputDBToUser(registerInputDB);
        User addedUser = userRepository.save(user);

        // generate access token and refresh token
        String accessToken = jwtService.generateAccessToken(addedUser.getId());
        String refreshToken = jwtService.generateRefreshToken(addedUser.getId());

        // save refresh token
        addedUser.setRefreshToken(refreshToken);
        userRepository.save(addedUser);

        // return login output
        return mapUser.mapUserToLoginOutput(
                addedUser,
                accessToken
        );
    }
}
