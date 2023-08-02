package it.univr.passportease.repository;

import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface WorkerRepository extends JpaRepository<Worker, UUID> {

}