package it.univr.passportease.service;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.Office;

import java.util.List;

public interface UserWorkerQueryService {

    List<Office> getOffices();

    List<Availability> getAvailabilities();
}
