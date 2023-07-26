package it.univr.passportease.repository;

import it.univr.passportease.entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestTypeRepository extends JpaRepository<RequestType, Integer> {


    @Query(value = "SELECT * FROM public.request_types", nativeQuery = true)
    List<RequestType> get();
}
