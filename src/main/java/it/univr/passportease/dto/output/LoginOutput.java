package it.univr.passportease.dto.output;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LoginOutput {
    private final UUID id;
    private final JWTSet jwtSet;
}
