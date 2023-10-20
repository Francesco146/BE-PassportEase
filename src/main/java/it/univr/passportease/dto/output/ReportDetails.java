package it.univr.passportease.dto.output;

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
    private String fiscalCode;
    private String name;
    private String surname;
    private String cityOfBirth;
    private Date dateOfBirth;
    private Date dateOfAvailability;
    private LocalTime startTime;
    private String nameRequestType;
    private String officeName;
    private String address;
}
