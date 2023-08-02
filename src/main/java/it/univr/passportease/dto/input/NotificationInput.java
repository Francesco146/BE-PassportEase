package it.univr.passportease.dto.input;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.RequestType;
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
    private String office;
    //TODO check requestTypeId to Name
    private String requestTypeName;
}
