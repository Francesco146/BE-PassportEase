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

/**
 * Implementation of {@link WorkerMutationService}. Provides methods to create, modify and delete requests.
 */
@Service
@Log4j2
@AllArgsConstructor
public class WorkerMutationServiceImpl implements WorkerMutationService {
    /**
     * JWT service to extract worker id from jwt access token.
     */
    private final JwtService jwtService;

    /**
     * Repository for {@link Worker} entity.
     */
    private final WorkerRepository workerRepository;
    /**
     * Repository for {@link RequestType} entity.
     */
    private final RequestTypeRepository requestTypeRepository;
    /**
     * Repository for {@link Request} entity.
     */
    private final RequestRepository requestRepository;
    /**
     * Repository for {@link Availability} entity.
     */
    private final AvailabilityRepository availabilityRepository;
    /**
     * Repository for {@link Office} entity.
     */
    private final OfficeRepository officeRepository;
    /**
     * Repository for {@link OfficeWorkingDay} entity.
     */
    private final OfficeWorkingDayRepository officeWorkingDayRepository;
    /**
     * Repository for {@link RequestOffice} entity.
     */
    private final RequestOfficeRepository requestOfficeRepository;
    /**
     * Repository for {@link Notification} entity.
     */
    private final NotificationRepository notificationRepository;

    /**
     * Service to map {@link RequestInput} to {@link Request}.
     */
    private final MapRequest mapRequest;
    /**
     * Service to map {@link String} to {@link RequestType}.
     */
    private final MapRequestType mapRequestType;
    /**
     * Service to map {@link Request} and {@link Office} to {@link RequestOffice}.
     */
    private final MapRequestOffice mapRequestOffice;
    /**
     * Service to map {@link Request} to {@link Availability}.
     */
    private final MapAvailability mapAvailability;

    /**
     * Get notification for user that his request has been deleted.
     *
     * @param request      request
     * @param availability availability
     * @return notification for user that his request has been deleted
     */
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

    /**
     * Create request.
     *
     * @param token        jwt access token
     * @param requestInput request input, contains request data as {@link RequestInput}
     * @return created request as {@link Request}
     * @throws WorkerNotFoundException     if worker is not found
     * @throws InvalidRequestTypeException if request type is invalid
     * @throws OfficeOverloadedException   if office doesn't have enough workers
     */
    @Override
    @PreAuthorize("hasAuthority('WORKER') && hasAuthority('VALIDATED')")
    public Request createRequest(JWT token, RequestInput requestInput)
            throws WorkerNotFoundException, InvalidRequestTypeException, OfficeOverloadedException {

        Worker worker = workerRepository.findById(jwtService.extractId(token))
                .orElseThrow(WorkerNotFoundException::new);

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

    /**
     * Get request type if exists, otherwise create it and return it.
     *
     * @param requestTypeName request type name
     * @return request type if exists, otherwise create it and return it
     * @throws InvalidRequestTypeException if request type is invalid
     */
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

    /**
     * Check if there are not enough workers for the request, by checking if there are more requests than workers in
     * each office.
     *
     * @param requestInput request input, contains request data as {@link RequestInput}
     * @param offices      offices
     * @return {@code true} if there are not enough workers for the request, otherwise {@code false}
     */
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

    /**
     * Count busy workers in office, between start and end date and time. It does it by counting the number of requests
     * in the office that are between start and end date and time.
     *
     * @param office       office
     * @param requestInput request input, contains request data as {@link RequestInput}
     * @return number of busy workers in office, between start and end date and time. It does it by counting the number
     * of requests in the office that are between start and end date and time.
     */
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

    /**
     * Set notifications as {@code ready = true} if they are between start and end date and time and {@code ready = false}
     * otherwise.
     *
     * @param startDate   start date
     * @param endDate     end date
     * @param offices     offices
     * @param requestType request type
     */
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

    /**
     * Create availabilities for each office between start and end date and time.
     *
     * @param startDate start date
     * @param endDate   end date
     * @param offices   offices
     * @param request   request
     */
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

        iterate(start, date -> date.isBefore(end), date -> date.plusDays(1))
                .forEach(date -> {
                    Day day = Day.valueOf(date.getDayOfWeek().toString());
                    processOfficeWorkingDays(
                            day,
                            officeWorkingDays,
                            date,
                            request
                    );
                });
    }

    /**
     * Process office working days for a specific day. By processing it means to create availabilities for each office
     * working day between start and end date and time.
     *
     * @param day               day
     * @param officeWorkingDays office working days
     * @param date              date
     * @param request           request
     */
    private void processOfficeWorkingDays(Day day, List<OfficeWorkingDay> officeWorkingDays, LocalDate date, Request request) {
        officeWorkingDays
                .stream()
                .filter(officeWorkingDay ->
                        officeWorkingDay.getDay().equals(day))
                .forEach(officeWorkingDay ->
                        processOfficeWorkingDayTimeSlot(officeWorkingDay, date, request)
                );
    }

    /**
     * Process office working day time slot. By processing it means to create availabilities for each office working
     * day time slot between start and end time.
     *
     * @param officeWorkingDay office working day
     * @param date             date
     * @param request          request
     */
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


    /**
     * Get higher time between time 1 and time 2, that is the time that is after the other.
     *
     * @param time1 time 1, used as office start time
     * @param time2 time 2, used as request start time
     * @return higher time between time 1 and time 2, that is the time that is after the other
     */
    private LocalTime getHigherTime(LocalTime time1, LocalTime time2) {
        return time1.isAfter(time2) ? time1 : time2;
    }

    /**
     * Get lower time between time 1 and time 2, that is the time that is before the other.
     *
     * @param time1 time 1, used as office end time
     * @param time2 time 2, used as request end time
     * @return lower time between time 1 and time 2, that is the time that is before the other
     */
    private LocalTime getLowerTime(LocalTime time1, LocalTime time2) {
        return time1.isBefore(time2) ? time1 : time2;
    }

    /**
     * Check if request date is inside notification date, by checking if the range of request date is inside the range
     * of notification date. Similar to {@link #isTimeInsideTime(LocalTime, LocalTime, LocalTime, LocalTime)}.
     *
     * @param requestStartDate      request start date
     * @param requestEndDate        request end date
     * @param notificationStartDate notification start date
     * @param notificationEndDate   notification end date
     * @return {@code true} if request date is inside notification date, otherwise {@code false}
     */
    private boolean isRequestDateInsideNotificationDate(Date requestStartDate, Date requestEndDate,
                                                        Date notificationStartDate, Date notificationEndDate) {

        return !(notificationEndDate.before(requestStartDate) || notificationStartDate.after(requestEndDate));
    }

    /**
     * Check if request time is inside office time, by checking if the range of request time is inside the range of
     * office time. Similar to {@link #isRequestDateInsideNotificationDate(Date, Date, Date, Date)}.
     *
     * @param requestStartTime request start time
     * @param requestEndTime   request end time
     * @param startTime        office start time
     * @param endTime          office end time
     * @return {@code true} if request time is inside office time, otherwise {@code false}
     */
    private boolean isTimeInsideTime(LocalTime requestStartTime, LocalTime requestEndTime, LocalTime startTime,
                                     LocalTime endTime) {

        return !(endTime.isBefore(requestStartTime) || startTime.isAfter(requestEndTime));
    }

    /**
     * Modify request.
     *
     * @param token        jwt access token
     * @param requestID    request id
     * @param requestInput request input, contains request data as {@link RequestInput}
     * @return modified request as {@link Request}
     * @throws WorkerNotFoundException   if worker is not found
     * @throws RequestNotFoundException  if request is not found
     * @throws OfficeOverloadedException if office doesn't have enough workers
     */
    @Override
    public Request modifyRequest(JWT token, String requestID, RequestInput requestInput)
            throws WorkerNotFoundException, RequestNotFoundException, OfficeOverloadedException {
        // get worker from db
        Worker worker = workerRepository.findById(jwtService.extractId(token)).orElseThrow(WorkerNotFoundException::new);

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

    /**
     * Delete request.
     *
     * @param token     jwt access token
     * @param requestID request id
     * @throws WorkerNotFoundException  if worker is not found
     * @throws RequestNotFoundException if request is not found
     */
    @Override
    public void deleteRequest(JWT token, String requestID)
            throws WorkerNotFoundException, RequestNotFoundException {

        Optional<Worker> worker = workerRepository.findById(jwtService.extractId(token));
        if (worker.isEmpty())
            throw new WorkerNotFoundException();

        Request request = requestRepository.findById(UUID.fromString(requestID)).orElseThrow(() ->
                new RequestNotFoundException("Request not found")
        );


        deleteAvailabilities(request);

        // delete request
        requestRepository.delete(request);
    }

    /**
     * Delete availabilities and requestOffice associated to a request.
     *
     * @param request request
     */
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
