package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * Class that represents the input of a request. From the user.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class RequestInput {
    /**
     * The duration of the request in minutes.
     */
    private long duration;
    /**
     * The start date of the request.
     */
    private Date startDate;
    /**
     * The end date of the request.
     */
    private Date endDate;
    /**
     * The start time of the request.
     */
    private LocalTime startTime;
    /**
     * The end time of the request.
     */
    private LocalTime endTime;
    /**
     * The type of the request.
     */
    private String requestType;
    /**
     * The offices of the request.
     */
    private List<String> offices;
}
