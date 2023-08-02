package it.univr.passportease.service.worker;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;

public interface WorkerMutationService {
    Request createRequest(String token, RequestInput requestInput);
}
