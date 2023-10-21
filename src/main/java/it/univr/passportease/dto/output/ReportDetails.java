package it.univr.passportease.dto.output;

import it.univr.passportease.entity.RequestType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.Date;

/**
 * This class represents the details of a report. Used to show the details of a report in the report page.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ReportDetails {
    /**
     * The fiscal code of the user.
     */
    private String fiscalCode;
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
     * The date of availability requested by the user.
     */
    private Date dateOfAvailability;
    /**
     * The time of availability requested by the user.
     */
    private LocalTime startTime;
    /**
     * The {@link RequestType} of the request.
     */
    private String nameRequestType;
    /**
     * The name of the office.
     */
    private String officeName;
    /**
     * The address of the office.
     */
    private String address;
}
