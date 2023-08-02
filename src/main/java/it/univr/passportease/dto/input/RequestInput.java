package it.univr.passportease.dto.input;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class RequestInput {
    private Integer duration;
    private Date startDate;
    private Date endDate;
    private String requestType;
    private List<String> offices;
}
