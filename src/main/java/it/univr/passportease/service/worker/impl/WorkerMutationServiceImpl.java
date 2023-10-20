package it.univr.passportease.service.worker.impl;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.*;
import it.univr.passportease.entity.enums.Day;
import it.univr.passportease.entity.enums.Status;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.RequestNotFoundException;
import it.univr.passportease.exception.notfound.WorkerNotFoundException;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.helper.map.MapAvailability;
import it.univr.passportease.helper.map.MapRequest;
import it.univr.passportease.helper.map.MapRequestOffice;
import it.univr.passportease.helper.map.MapRequestType;
import it.univr.passportease.repository.*;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.worker.WorkerMutationService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Stream.iterate;

@Service
@Log4j2
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

    @NotNull
    private static Notification getNotification(Request request, Availability availability) {
        Notification notification = new Notification();
        notification.setIsReady(true);
        notification.setMessage("La tua richiesta è stata cancellata");
        notification.setStartDate(new Date());
        notification.setEndDate(new Date());
        notification.setOffice(availability.getOffice());
        notification.setUser(availability.getUser());
        notification.setRequestType(request.getRequestType());
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());
        return notification;
    }

    @Override
    @PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public Request createRequest(JWT token, RequestInput requestInput)
            throws WorkerNotFoundException, InvalidRequestTypeException, OfficeOverloadedException {

        Worker worker = workerRepository.findById(jwtService.extractId(token))
                .orElseThrow(() ->
                        new WorkerNotFoundException("Worker not found")
                );

        String requestTypeName = requestInput.getRequestType();
        List<Office> offices = officeRepository.findAllByNameIn(requestInput.getOffices());
        Date startDate = requestInput.getStartDate();
        Date endDate = requestInput.getEndDate();

        if (isWorkersNotEnoughForRequest(requestInput, offices))
            throw new OfficeOverloadedException("Office doesn't have enough workers");

        RequestType requestType = getOrCreateRequestType(requestTypeName);

        Request request = mapRequest.mapRequestInputToRequest(requestInput, requestType, worker);
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
        Optional<RequestType> requestType = requestTypeRepository.findByName(requestTypeName);
        if (requestType.isEmpty()) {
            RequestType newRequestType = mapRequestType.mapStringToRequestType(requestTypeName);
            requestTypeRepository.save(newRequestType);
            requestType = requestTypeRepository.findByName(requestTypeName);
        }

        if (requestType.isEmpty())
            throw new InvalidRequestTypeException("Error while creating request type");

        return requestType.get();
    }

    private boolean isWorkersNotEnoughForRequest(RequestInput requestInput, List<Office> offices) {

        for (Office office : offices) {
            long totalNumberOfWorker = workerRepository.countByOffice(office);
            long busyWorkers = countBusyWorkersInOffice(office, requestInput);

            log.info("totalNumberOfWorker: " + totalNumberOfWorker);
            log.info("busyWorkers: " + busyWorkers);

            if (busyWorkers >= totalNumberOfWorker) {
                log.info("Office " + office.getName() + " doesn't have enough workers");
                return true;
            }
        }
        return false;
    }

    private long countBusyWorkersInOffice(Office office, RequestInput requestInput) {
        Date startDate = requestInput.getStartDate();
        Date endDate = requestInput.getEndDate();
        LocalTime requestStartTime = requestInput.getStartTime();
        LocalTime requestEndTime = requestInput.getEndTime();

        long busyWorkers = 0;
        List<Request> requests = requestRepository.getOfficeRequests(office.getId(), startDate, endDate);

        for (Request request : requests)
            if (isRequestDateInsideNotificationDate(startDate, endDate, request.getStartDate(), request.getEndDate())) {
                LocalTime startTime = request.getStartTime();
                LocalTime endTime = request.getEndTime();
                if (isTimeInsideTime(requestStartTime, requestEndTime, startTime, endTime))
                    busyWorkers++;
            }

        log.info("Office " + office.getName() + " - busyWorkers: " + busyWorkers);
        return busyWorkers;
    }

    private void setNotifications(Date startDate, Date endDate, List<Office> offices, RequestType requestType) {
        offices.stream()
                .map(office -> notificationRepository.findAllByOfficeAndIsReadyAndRequestType(
                        office, false, requestType))
                .forEach(notifications ->
                        notifications.stream()
                                .filter(notification ->
                                        isRequestDateInsideNotificationDate(startDate, endDate, notification.getStartDate(), notification.getEndDate())
                                                && !notification.getIsReady()
                                )
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

        List<OfficeWorkingDay> officeWorkingDays = offices
                .stream()
                .flatMap(
                        office ->
                                officeWorkingDayRepository
                                        .findByOffice(office)
                                        .stream()
                )
                .toList();

        iterate(start, date -> date.isBefore(end), date -> date.plusDays(1)).forEach(date -> {
            Day day = Day.valueOf(date.getDayOfWeek().toString());
            processOfficeWorkingDays(
                    day,
                    officeWorkingDays,
                    date,
                    request
            );
        });
    }

    private void processOfficeWorkingDays(Day day, List<OfficeWorkingDay> officeWorkingDays, LocalDate date, Request request) {
        officeWorkingDays
                .stream()
                .filter(officeWorkingDay ->
                        officeWorkingDay.getDay().equals(day))
                .forEach(officeWorkingDay ->
                        processOfficeWorkingDay(officeWorkingDay, date, request)
                );
    }

    private void processOfficeWorkingDay(OfficeWorkingDay officeWorkingDay, LocalDate date, Request request) {

        processOfficeWorkingDayTimeSlot(officeWorkingDay, date, request);

        if (officeWorkingDay.getStartTime2() != null && officeWorkingDay.getEndTime2() != null)
            processOfficeWorkingDayTimeSlot(officeWorkingDay, date, request);
    }

    private void processOfficeWorkingDayTimeSlot(OfficeWorkingDay officeWorkingDay, LocalDate date, Request request) {

        LocalTime requestStartTime = request.getStartTime();
        LocalTime requestEndTime = request.getEndTime();

        long duration = request.getDuration();

        LocalTime officeStartTime = officeWorkingDay.getStartTime2();
        LocalTime officeEndTime = officeWorkingDay.getEndTime2();
        Office office = officeWorkingDay.getOffice();


        LocalTime startTime = getHigherTime(officeStartTime, requestStartTime);
        LocalTime endTime = getLowerTime(officeEndTime, requestEndTime);


        iterate(startTime, time -> time.isBefore(endTime), time -> time.plusMinutes(duration))
                .map(time ->
                        mapAvailability.mapRequestToAvailability(request, office, date, time)
                )
                .forEach(availabilityRepository::save);
    }


    private LocalTime getHigherTime(LocalTime time1, LocalTime time2) {
        return time1.isAfter(time2) ? time1 : time2;
    }

    private LocalTime getLowerTime(LocalTime time1, LocalTime time2) {
        return time1.isBefore(time2) ? time1 : time2;
    }

    private boolean isRequestDateInsideNotificationDate(Date requestStartDate, Date requestEndDate,
                                                        Date notificationStartDate, Date notificationEndDate) {

        return !(notificationEndDate.before(requestStartDate) || notificationStartDate.after(requestEndDate));
    }

    private boolean isTimeInsideTime(LocalTime requestStartTime, LocalTime requestEndTime, LocalTime startTime,
                                     LocalTime endTime) {

        return !(endTime.isBefore(requestStartTime) || startTime.isAfter(requestEndTime));
    }

    @Override
    public Request modifyRequest(JWT token, String requestID, RequestInput requestInput)
            throws WorkerNotFoundException, RequestNotFoundException, OfficeOverloadedException {
        // get worker from db
        Worker worker = workerRepository.findById(jwtService.extractId(token)).orElseThrow(() ->
                new WorkerNotFoundException("Worker not found")
        );

        Request request = requestRepository.findById(UUID.fromString(requestID)).orElseThrow(() ->
                new RequestNotFoundException("Request not found")
        );

        List<Office> offices = officeRepository.findAllByNameIn(requestInput.getOffices());

        if (isWorkersNotEnoughForRequest(requestInput, offices))
            throw new OfficeOverloadedException("Office doesn't have enough workers");

        // delete and ricreate availabilities
        deleteAvailabilities(request);

        // update request
        String requestTypeName = requestInput.getRequestType();
        RequestType requestType = getOrCreateRequestType(requestTypeName);
        request = mapRequest.mapRequestInputToRequest(requestInput, requestType, worker);
        request = requestRepository.save(request);

        // create new availabilities
        createAvailabilities(requestInput.getStartDate(), requestInput.getEndDate(), offices, request);

        return request;
    }

    @Override
    public void deleteRequest(JWT token, String requestID)
            throws WorkerNotFoundException, RequestNotFoundException {

        Optional<Worker> worker = workerRepository.findById(jwtService.extractId(token));
        if (worker.isEmpty())
            throw new WorkerNotFoundException("Worker not found");

        Request request = requestRepository.findById(UUID.fromString(requestID)).orElseThrow(() ->
                new RequestNotFoundException("Request not found")
        );


        deleteAvailabilities(request);

        // delete request
        requestRepository.delete(request);
    }

    private void deleteAvailabilities(Request request) {
        // delete and ricreate availabilities
        List<Availability> availabilities = availabilityRepository.findByRequestId(request.getId());

        availabilities.forEach(availability -> {
                    // create user notification
                    if (availability.getUser() != null)
                        notificationRepository.save(getNotification(request, availability));
                    if (!availability.getStatus().equals(Status.TIMEDOUT))
                        availabilityRepository.delete(availability);
                }
        );

        // delete requestOffice
        List<RequestOffice> requestOffices = requestOfficeRepository.findByRequestId(request.getId());
        requestOfficeRepository.deleteAll(requestOffices);
    }
}
