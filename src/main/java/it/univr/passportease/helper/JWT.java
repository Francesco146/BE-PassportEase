package it.univr.passportease.helper;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode
@ToString
@Getter
public class JWT {
    private static final String JWT_REGEX = "^[\\w-]*\\.[\\w-]*\\.[\\w-]*$";
    private String token;
}
