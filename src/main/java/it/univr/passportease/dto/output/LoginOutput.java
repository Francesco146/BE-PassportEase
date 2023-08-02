package it.univr.passportease.dto.output;


import java.util.UUID;


public record LoginOutput(UUID id, JWTSet jwtSet) {
}
