package it.univr.passportease.dto.input;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class NotificationInputDB {
    private NotificationInput notificationInput;
    private User user;
    private Office office;
    private RequestType requestType;
}
