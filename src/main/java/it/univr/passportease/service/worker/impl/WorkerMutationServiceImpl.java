package it.univr.passportease.service.worker.impl;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.*;
import it.univr.passportease.entity.enums.Day;
import it.univr.passportease.entity.enums.Status;
import it.univr.passportease.exception.illegalstate.OfficeOverloadedException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.RequestNotFoundException;
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
        if (worker.isEmpty())
            throw new WorkerNotFoundException("Worker not found");

        String requestTypeName = requestInput.getRequestType();
        List<Office> offices = officeRepository.findAllByNameIn(requestInput.getOffices());
        Date startDate = requestInput.getStartDate();
        Date endDate = requestInput.getEndDate();

        if (!isWorkersEnoughForRequest(requestInput, offices))
            throw new OfficeOverloadedException("Office doesn't have enough workers");

        RequestType requestType = getOrCreateRequestType(requestTypeName);

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

    private Boolean isWorkersEnoughForRequest(RequestInput requestInput, List<Office> offices)
            throws OfficeOverloadedException {
        Date startDate = requestInput.getStartDate();
        Date endDate = requestInput.getEndDate();
        LocalTime requestStartTime = requestInput.getStartTime();
        LocalTime requestEndTime = requestInput.getEndTime();

        for (Office office : offices) {
            long totalNumberOfWorker = workerRepository.countByOffice(office);
            System.out.println("totalNumberOfWorker: " + totalNumberOfWorker);
            long busyWorkers = 0;
            List<Request> requests = requestRepository.getOfficeRequests(
                    office.getId(),
                    startDate,
                    endDate);

            for (Request request : requests) {
                if (isRequestDateInsideNotificationDate(startDate, endDate, request.getStartDate(),
                        request.getEndDate())) {
                    LocalTime startTime = request.getStartTime();
                    LocalTime endTime = request.getEndTime();
                    if (isTimeInsideTime(requestStartTime, requestEndTime, startTime, endTime))
                        busyWorkers++;
                }
            }
            System.out.println("busyWorkers: " + busyWorkers);
            if (busyWorkers >= totalNumberOfWorker) {
                System.out.println("Office " + office.getName() + " doesn't have enough workers");
                return false;
            }
        }
        return true;
    }

    private void setNotifications(Date startDate, Date endDate, List<Office> offices, RequestType requestType) {
        offices.stream()
                .map(office -> notificationRepository.findAllByOfficeAndIsReadyAndRequestType(
                        office, false, requestType))
                .forEach(notifications -> notifications.stream()
                        .filter(notification -> isRequestDateInsideNotificationDate(
                                startDate, endDate, notification.getStartDate(), notification.getEndDate()) &&
                                !notification.getIsReady())
                        .forEach(notification -> {
                            notification.setIsReady(true);
                            notificationRepository.save(notification);
                        }));
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

    private boolean isRequestDateInsideNotificationDate(Date requestStartDate, Date requestEndDate,
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

    private boolean isTimeInsideTime(LocalTime requestStartTime, LocalTime requestEndTime, LocalTime startTime,
            LocalTime endTime) {
        boolean isRequestStartTimeAfterStartTime = requestStartTime.isAfter(startTime);
        boolean isRequestStartTimeEqualsStartTime = requestStartTime.equals(startTime);
        boolean isRequestEndTimeBeforeEndTime = requestEndTime.isBefore(endTime);
        boolean isRequestEndTimeEqualsEndTime = requestEndTime.equals(endTime);
        boolean isRequestStartTimeBeforeEndTime = requestStartTime.isBefore(endTime);
        boolean isRequestEndTimeAfterEndTime = requestEndTime.isAfter(endTime);
        boolean isRequestStartTimeBeforeStartTime = requestStartTime.isBefore(startTime);
        boolean isRequestEndTimeAfterStartTime = requestEndTime.isAfter(startTime);

        // 1. requestTimes are between startTime and endTime
        if ((isRequestStartTimeAfterStartTime || isRequestStartTimeEqualsStartTime) &&
                (isRequestEndTimeBeforeEndTime || isRequestEndTimeEqualsEndTime))
            return true;

        // 2. requestStartTime is after startTime but before endTime and requestEndTime
        // is after endTime
        if (isRequestStartTimeAfterStartTime &&
                isRequestStartTimeBeforeEndTime &&
                isRequestEndTimeAfterEndTime)
            return true;

        // 3. requestStartTime is before startTime and requestEndTime is after startTime
        return isRequestStartTimeBeforeStartTime && isRequestEndTimeAfterStartTime;
    }

    @Override
    public Request modifyRequest(String token, String requestID, RequestInput requestInput) {
        // get worker from db
        UUID workerId = jwtService.extractId(token);
        Optional<Worker> worker = workerRepository.findById(workerId);
        if (worker.isEmpty())
            throw new WorkerNotFoundException("Worker not found");

        Optional<Request> _request = requestRepository.findById(UUID.fromString(requestID));
        if (_request.isEmpty())
            throw new RequestNotFoundException("Request not found");

        Request request = _request.get();

        // delete and ricreate availabilities
        deleteAvailabilities(request);

        // update request
        String requestTypeName = requestInput.getRequestType();
        RequestType requestType = getOrCreateRequestType(requestTypeName);
        request = mapRequest.mapRequestInputToRequest(requestInput, requestType, worker.get());
        request = requestRepository.save(request);

        // create new availabilities
        List<Office> offices = officeRepository.findAllByNameIn(requestInput.getOffices());
        createAvailabilities(requestInput.getStartDate(), requestInput.getEndDate(), offices, request);

        return request;
    }

    @Override
    public void deleteRequest(String token, String requestID) {
        // get worker from db
        UUID workerId = jwtService.extractId(token);
        Optional<Worker> worker = workerRepository.findById(workerId);
        if (worker.isEmpty())
            throw new WorkerNotFoundException("Worker not found");

        Optional<Request> _request = requestRepository.findById(UUID.fromString(requestID));
        if (_request.isEmpty())
            throw new RequestNotFoundException("Request not found");

        Request request = _request.get();

        deleteAvailabilities(request);

        // delete request
        requestRepository.delete(request);
    }

    private void deleteAvailabilities(Request request) {
        // delete and ricreate availabilities
        List<Availability> availabilities = availabilityRepository.findByRequestId(request.getId());
        for (Availability availability : availabilities) {
            if (availability.getUser() != null) {
                // create user notification
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
                notificationRepository.save(notification);
            }
            if (!availability.getStatus().equals(Status.TIMEDOUT))
                availabilityRepository.delete(availability);
        }

        // delete requestOffice
        List<RequestOffice> requestOffices = requestOfficeRepository.findByRequestId(request.getId());
        for (RequestOffice requestOffice : requestOffices) {
            requestOfficeRepository.delete(requestOffice);
        }
    }
}
