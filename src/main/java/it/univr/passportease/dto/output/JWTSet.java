package it.univr.passportease.dto.output;

/**
 * @param accessToken  JWT access token
 * @param refreshToken JWT refresh token
 */
public record JWTSet(String accessToken, String refreshToken) {
}
