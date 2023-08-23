package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class AvailabilityFilters {
    private List<OfficeInput> offices;
    private List<String> requestTypes;
    private Date startDate
}
