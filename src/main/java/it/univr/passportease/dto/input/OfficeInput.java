package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class OfficeInput {
    private String name;
    private String address;
}
