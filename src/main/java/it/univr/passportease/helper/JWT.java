package it.univr.passportease.helper;

import lombok.*;

/**
 * This class represents a JWT token.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode
@ToString
@Getter
public class JWT {
    /**
     * The JWT token as a string.
     */
    private String token;
}
