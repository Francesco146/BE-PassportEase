package it.univr.passportease.service.worker;

import it.univr.passportease.entity.RequestType;

import java.util.List;

public interface WorkerQueryService {

    List<RequestType> getAllRequestTypes();
}
