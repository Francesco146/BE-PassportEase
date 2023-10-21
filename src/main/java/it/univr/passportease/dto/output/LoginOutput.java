package it.univr.passportease.dto.output;


import java.util.UUID;


/**
 * DTO containing the output of the login operation.
 *
 * @param id     user or worker id
 * @param jwtSet {@link JWTSet} containing the JWTs
 */
public record LoginOutput(UUID id, JWTSet jwtSet) {
}
