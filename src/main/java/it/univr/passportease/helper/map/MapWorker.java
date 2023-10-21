package it.univr.passportease.helper.map;

import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.helper.JWT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Map {@link Worker} to {@link LoginOutput}
 */
@AllArgsConstructor
@Component
public class MapWorker {
    /**
     * Map {@link Worker} to {@link LoginOutput}
     *
     * @param worker      {@link Worker} to map
     * @param accessToken {@link JWT} to map
     * @return {@link LoginOutput} mapped
     */
    public LoginOutput mapWorkerToLoginOutput(Worker worker, JWT accessToken) {
        return new LoginOutput(
                worker.getId(),
                new JWTSet(
                        accessToken.getToken(),
                        worker.getRefreshToken()
                )
        );
    }
}
