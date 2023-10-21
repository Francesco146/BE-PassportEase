package it.univr.passportease.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * WorkerInput is the class that represents the input data from the worker. Used to log in the worker.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class WorkerInput {
    /**
     * The username of the worker.
     */
    private String username;
    /**
     * The email of the worker.
     */
    private String email;
    /**
     * The password of the worker.
     */
    private String password;
    /**
     * The name of the worker.
     */
    private String officeName;
}
