package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class is used to store the data of an office input from the user.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class OfficeInput {
    private String name;
    private String address;
}
