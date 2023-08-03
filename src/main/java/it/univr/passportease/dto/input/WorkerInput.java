package it.univr.passportease.dto.input;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class WorkerInput {
    private String username;
    private String email;
    private String password;
    private String officeName;
}
