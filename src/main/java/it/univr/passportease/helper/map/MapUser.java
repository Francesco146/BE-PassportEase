package it.univr.passportease.helper.map;

import it.univr.passportease.dto.input.NotificationInput;
import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.input.RegisterInputDB;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.User;
import it.univr.passportease.repository.OfficeRepository;
import it.univr.passportease.repository.RequestTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@AllArgsConstructor
@Component
public class MapUser {

    private final RequestTypeRepository requestTypeRepository;
    private final OfficeRepository officeRepository;

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
                        user.getRefreshToken()
                )
        );
    }

    public Notification mapNotificationInputToNotification(NotificationInput notificationInput, User user) {
        if (officeRepository.findByName(notificationInput.getOfficeName()).isEmpty())
            throw new RuntimeException("Invalid Office");
        if (requestTypeRepository.findByName(notificationInput.getRequestTypeName()).isEmpty())
            throw new RuntimeException("Invalid Request Type Name");

        Notification notification = new Notification();
        notification.setIsReady(false);
        notification.setStartDate(notificationInput.getStartDate());
        notification.setEndDate(notificationInput.getEndDate());
        notification.setOffice(
                officeRepository.findByName(
                        notificationInput.getOfficeName()
                ).get()
        );
        notification.setUser(user);
        notification.setRequestType(
                requestTypeRepository.findByName(
                        notificationInput.getRequestTypeName()
                ).get()
        );
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());

        return notification;
    }
}
