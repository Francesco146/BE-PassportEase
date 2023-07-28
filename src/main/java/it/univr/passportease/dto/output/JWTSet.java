package it.univr.passportease.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class JWTSet {
    private final String accessToken;
    private final String refreshToken;
}
