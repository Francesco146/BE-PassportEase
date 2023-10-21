package it.univr.passportease.dto.output;

/**
 * Record containing the JWTs
 *
 * @param accessToken  JWT access token
 * @param refreshToken JWT refresh token
 */
public record JWTSet(String accessToken, String refreshToken) {
}
