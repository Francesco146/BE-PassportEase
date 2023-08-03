package it.univr.passportease.service.worker.impl;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.*;
import it.univr.passportease.entity.enums.Day;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
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

    private final WorkerRepository workerRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final RequestRepository requestRepository;
    private final AvailabilityRepository availabilityRepository;
    private final OfficeRepository officeRepository;
    private final OfficeWorkingDayRepository officeWorkingDayRepository;
    private final RequestOfficeRepository requestOfficeRepository;
    private final NotificationRepository notificationRepository;

    private final MapRequest mapRequest;
    private final MapRequestType mapRequestType;
    private final MapRequestOffice mapRequestOffice;
    private final MapAvailability mapAvailability;

    @Override
    @PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public Request createRequest(String token, RequestInput requestInput)
            throws WorkerNotFoundException, InvalidRequestTypeException, OfficeOverloadedException {
        // get worker from db
        UUID workerId = jwtService.extractId(token);
        Optional<Worker> worker = workerRepository.findById(workerId);
        if (worker.isEmpty()) throw new WorkerNotFoundException("Worker not found");

        String requestTypeName = requestInput.getRequestType();
        // delete this
        // System.out.println(requestTypeName);
        List<Office> offices = officeRepository.findAllByNameIn(requestInput.getOffices());
        // delete this
        // for (Office office : offices) System.out.println(office.getName());
        Date startDate = requestInput.getStartDate();
        // delete this
        // System.out.println(startDate);
        Date endDate = requestInput.getEndDate();
        // delete this
        // System.out.println(endDate);

        if (!isWorkersEnoughForRequest(startDate, endDate, offices)) return null;

        RequestType requestType = getOrCreateRequestType(requestTypeName);
        // delete this
        // System.out.println(requestType.getName());

        Request request = mapRequest.mapRequestInputToRequest(requestInput, requestType, worker.get());
        request = requestRepository.save(request);
        for (Office office : offices) {
            RequestOffice requestOffice = mapRequestOffice.mapRequestAndOfficeToRequestOffice(request, office);
            requestOfficeRepository.save(requestOffice);
        }

        setNotifications(startDate, endDate, offices, requestType);

        createAvailabilities(startDate, endDate, offices, request);

        return request;
    }

    private RequestType getOrCreateRequestType(String requestTypeName) throws InvalidRequestTypeException {
        Optional<RequestType> _requestType = requestTypeRepository.findByName(requestTypeName);
        if (_requestType.isEmpty()) {
            RequestType newRequestType = mapRequestType.mapStringToRequestType(requestTypeName);
            requestTypeRepository.save(newRequestType);
            _requestType = requestTypeRepository.findByName(requestTypeName);
        }

        if (_requestType.isEmpty()) throw new InvalidRequestTypeException("Error while creating request type");

        return _requestType.get();
    }

    private Boolean isWorkersEnoughForRequest(Date startDate, Date endDate, List<Office> offices)
            throws OfficeOverloadedException {
        // delete this
        // System.out.println("totalNumberOfWorker: " + totalNumberOfWorker);
        // delete this
        // System.out.println("busyWorkers: " + busyWorkers);
        offices.forEach(office -> {
            long totalNumberOfWorker = workerRepository.countByOffice(office);
            long busyWorkers = requestRepository.countBusyWorkers(
                    office.getId(),
                    startDate,
                    endDate
            );
            if (busyWorkers >= totalNumberOfWorker)
                throw new OfficeOverloadedException("Office " + office.getName() + " doesn't have enough workers");
        });
        return true;
    }

    private void setNotifications(Date startDate, Date endDate, List<Office> offices, RequestType requestType) {
        // delete this
        // System.out.println("notification: " + notification.getId());
        offices.stream()
                .map(office -> notificationRepository.findAllByOfficeAndIsReadyAndRequestType(
                                office, false, requestType
                        )
                )
                .forEach(notifications ->
                        notifications.stream()
                                .filter(notification -> isRequestDateInsideNotificationDate(
                                        startDate, endDate, notification.getStartDate(), notification.getEndDate()) &&
                                        !notification.getIsReady())
                                .forEach(notification -> {
                                            notification.setIsReady(true);
                                            notificationRepository.save(notification);
                                        }
                                )
                );
    }

    private void createAvailabilities(Date startDate, Date endDate, List<Office> offices, Request request) {
        // cast dates to LocalDate
        LocalDate start = startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        LocalTime requestStartTime = request.getStartTime();
        LocalTime requestEndTime = request.getEndTime();

        long duration = request.getDuration();

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
        return time1.isAfter(time2) ? time1 : time2;
    }

    private LocalTime getLowerTime(LocalTime time1, LocalTime time2) {
        return time1.isBefore(time2) ? time1 : time2;
    }

    private Boolean isRequestDateInsideNotificationDate(Date requestStartDate, Date requestEndDate,
                                                        Date notificationStartDate, Date notificationEndDate) {
        boolean isRequestStartDateAfterNotificationStartDate = requestStartDate.after(notificationStartDate);
        boolean isRequestStartDateEqualsNotificationStartDate = requestStartDate.equals(notificationStartDate);
        boolean isRequestEndDateBeforeNotificationEndDate = requestEndDate.before(notificationEndDate);
        boolean isRequestEndDateEqualsNotificationEndDate = requestEndDate.equals(notificationEndDate);
        boolean isRequestStartDateBeforeNotificationEndDate = requestStartDate.before(notificationEndDate);
        boolean isRequestEndDateAfterNotificationEndDate = requestEndDate.after(notificationEndDate);
        boolean isRequestStartDateBeforeNotificationStartDate = requestStartDate.before(notificationStartDate);
        boolean isRequestEndDateAfterNotificationStartDate = requestEndDate.after(notificationStartDate);


        // 1. requestDates are between notificationStartDate and notificationEndDate
        if ((isRequestStartDateAfterNotificationStartDate || isRequestStartDateEqualsNotificationStartDate) &&
                (isRequestEndDateBeforeNotificationEndDate || isRequestEndDateEqualsNotificationEndDate))
            return true;


        // 2. requestStartDate is after notificationStartDate but before
        // notificationEndDate and requestEndDate is after notificationEndDate
        if (isRequestStartDateAfterNotificationStartDate &&
                isRequestStartDateBeforeNotificationEndDate &&
                isRequestEndDateAfterNotificationEndDate)
            return true;

        // 3. requestStartDate is before notificationStartDate and requestEndDate is
        // after notificationStartDate
        return isRequestStartDateBeforeNotificationStartDate && isRequestEndDateAfterNotificationStartDate;
    }
}
