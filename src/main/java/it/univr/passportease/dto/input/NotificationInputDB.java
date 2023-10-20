package it.univr.passportease.dto.input;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class is used to store the data of a notification input, together with the data of the user, the office and the request type.
 * It is used to store the data of a notification input in the database.
 */
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
