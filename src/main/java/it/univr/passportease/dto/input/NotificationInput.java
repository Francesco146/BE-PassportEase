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

    /**
     * The start date of the period of time in which the user wants to be notified.
     */
    private Date startDate;
    /**
     * The end date of the period of time in which the user wants to be notified.
     */
    private Date endDate;
    /**
     * The name of the office in which the user wants to be notified.
     */
    private String officeName;
    /**
     * The name of the request type in which the user wants to be notified.
     */
    private String requestTypeName;
}
