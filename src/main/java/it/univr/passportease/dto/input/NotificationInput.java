package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Class used to represent the input of the notification service, that is the
 * input from the user.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class NotificationInput {

    private Date startDate;
    private Date endDate;
    private String officeName;
    private String requestTypeName;
}
