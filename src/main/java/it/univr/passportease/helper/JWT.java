package it.univr.passportease.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JWT {
    private static final String JWT_REGEX = "^[\\w-]*\\.[\\w-]*\\.[\\w-]*$";
    private String token;

    // equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JWT jwt)) return false;
        return token.equals(jwt.token);
    }

    // hashCode method
    @Override
    public int hashCode() {
        return token.hashCode();
    }
}
