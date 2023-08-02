package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

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
