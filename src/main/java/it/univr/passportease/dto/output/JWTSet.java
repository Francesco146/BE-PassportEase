package it.univr.passportease.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JWTSet {
    private String accessToken;
    private String refreshToken;
}
