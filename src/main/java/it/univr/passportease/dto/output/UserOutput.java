package it.univr.passportease.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserOutput {
    private final UUID id;
    private final String fiscalCode;
    private final String email;
    private final String name;
    private final String surname;
    private final String cityOfBirth;
    private final Date dateOfBirth;
}
