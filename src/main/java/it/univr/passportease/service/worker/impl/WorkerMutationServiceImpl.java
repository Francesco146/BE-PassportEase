package it.univr.passportease.service.worker.impl;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.*;
import it.univr.passportease.entity.enums.Day;
import it.univr.passportease.helper.map.MapAvailability;
import it.univr.passportease.helper.map.MapRequest;
import it.univr.passportease.helper.map.MapRequestOffice;
import it.univr.passportease.helper.map.MapRequestType;
import it.univr.passportease.repository.*;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.worker.WorkerMutationService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@AllArgsConstructor
public class WorkerMutationServiceImpl implements WorkerMutationService {
    private final JwtService jwtService;
    // repositories
    private final WorkerRepository workerRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final RequestRepository requestRepository;
    private final AvailabilityRepository availabilityRepository;
    private final OfficeRepository officeRepository;
    private final OfficeWorkingDayRepository officeWorkingDayRepository;
    private final RequestOfficeRepository requestOfficeRepository;
    private final NotificationRepository notificationRepository;
    // mappers
    private final MapRequest mapRequest;
    private final MapRequestType mapRequestType;
    private final MapRequestOffice mapRequestOffice;
    private final MapAvailability mapAvailability;

    @Override
    @PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public Request createRequest(String token, RequestInput requestInput) {
        // get worker from db
        UUID workerId = jwtService.extractId(token);
        Optional<Worker> worker = workerRepository.findById(workerId);
        if (worker.isEmpty()) {
            throw new RuntimeException("Worker not found");
        }
        // params in requestInput
        String requestTypeName = requestInput.getRequestType();
        List<Office> offices = officeRepository.findAllByNameIn(requestInput.getOffices());
        Date startDate = requestInput.getStartDate();
        Date endDate = requestInput.getEndDate();

        if (isWorkersEnoughForRequest(startDate, endDate, offices)) {
            RequestType requestType = getOrCreateRequestType(requestTypeName);

            Request request = mapRequest.mapRequestInputToRequest(requestInput, requestType, worker.get());
            request = requestRepository.save(request);
            for (Office office : offices) {
                RequestOffice requestOffice = mapRequestOffice.mapRequestAndOfficeToRequestOffice(request, office);
                requestOfficeRepository.save(requestOffice);
            }

            setNotifications(startDate, endDate, offices, requestType);

            createAvailabilities(startDate, endDate, offices, request);
        }

        return null;
    }

    private RequestType getOrCreateRequestType(String requestTypeName) {
        Optional<RequestType> _requestType = requestTypeRepository.findByName(requestTypeName);
        if (_requestType.isEmpty()) {
            RequestType newRequestType = mapRequestType.mapStringToRequestType(requestTypeName);
            requestTypeRepository.save(newRequestType);
            _requestType = requestTypeRepository.findByName(requestTypeName);
        }
        return _requestType.get();
    }

    private Boolean isWorkersEnoughForRequest(Date startDate, Date endDate, List<Office> offices) {
        for (Office office : offices) {
            Long totalNumberOfWorker = workerRepository.countByOffice(office);
            // TODO: delete this
            System.out.println("totalNumberOfWorker: " + totalNumberOfWorker);
            Long busyWorkers = requestRepository.countBusyWorkersByOfficeAndDataRange(office,
                    startDate,
                    endDate);

            // TODO: delete this
            System.out.println("busyWorkers: " + busyWorkers);
            if (totalNumberOfWorker > busyWorkers)
                throw new RuntimeException("Office " + office.getName() + " doesn't have enough workers");
        }
        return true;
    }

    private void setNotifications(Date startDate, Date endDate, List<Office> offices, RequestType requestType) {
        for (Office office : offices) {
            List<Notification> notifications = notificationRepository.findByOfficeAndIsReadyAndRequestType(office,
                    false, requestType);
            for (Notification notification : notifications) {
                if (notification.getStartDate().compareTo(startDate) >= 0 &&
                        notification.getEndDate().compareTo(endDate) <= 0) {
                    notification.setIsReady(true);
                    notificationRepository.save(notification);
                }
            }
        }
    }

    private void createAvailabilities(Date startDate, Date endDate, List<Office> offices, Request request) {
        // cast dates to LocalDate
        LocalDate start = startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        LocalTime requestStartTime = request.getStartTime();
        LocalTime requestEndTime = request.getEndTime();

        Integer duration = request.getDuration();

        List<OfficeWorkingDay> officeWorkingDays = new ArrayList<>();
        for (Office office : offices) {
            officeWorkingDays.addAll(officeWorkingDayRepository.findByOffice(office));
        }

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            String dayOfWeek = date.getDayOfWeek().toString();
            Day day = Day.valueOf(dayOfWeek);
            for (OfficeWorkingDay officeWorkingDay : officeWorkingDays) {
                if (officeWorkingDay.getDay().equals(day)) {
                    LocalTime officeStartTime1 = officeWorkingDay.getStartTime1();
                    LocalTime officeEndTime1 = officeWorkingDay.getEndTime1();

                    LocalTime startTime = getHigherTime(officeStartTime1, requestStartTime);
                    LocalTime endTime = getLowerTime(officeEndTime1, requestEndTime);

                    for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(duration)) {
                        Availability availability = mapAvailability.mapRequestToAvailability(request,
                                officeWorkingDay.getOffice(), date, time);
                        availabilityRepository.save(availability);
                    }

                    LocalTime officeStartTime2 = officeWorkingDay.getStartTime2();
                    LocalTime officeEndTime2 = officeWorkingDay.getEndTime2();
                    if (officeStartTime2 != null && officeEndTime2 != null) {
                        startTime = getHigherTime(officeStartTime2, requestStartTime);
                        endTime = getLowerTime(officeEndTime2, requestEndTime);

                        for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(duration)) {
                            Availability availability = mapAvailability.mapRequestToAvailability(request,
                                    officeWorkingDay.getOffice(), date, time);
                            availabilityRepository.save(availability);
                        }
                    }
                }
            }
        }

    }

    private LocalTime getHigherTime(LocalTime time1, LocalTime time2) {
        if (time1.isAfter(time2)) {
            return time1;
        } else {
            return time2;
        }
    }

    private LocalTime getLowerTime(LocalTime time1, LocalTime time2) {
        if (time1.isBefore(time2)) {
            return time1;
        } else {
            return time2;
        }
    }

}
