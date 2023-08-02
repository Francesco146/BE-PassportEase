package it.univr.passportease.service.worker;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Notification;
import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.OfficeWorkingDay;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestOffice;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.helper.map.MapRequest;
import it.univr.passportease.helper.map.MapRequestOffice;
import it.univr.passportease.helper.map.MapRequestType;
import it.univr.passportease.repository.AvailabilityRepository;
import it.univr.passportease.repository.NotificationRepository;
import it.univr.passportease.repository.OfficeRepository;
import it.univr.passportease.repository.OfficeWorkingDayRepository;
import it.univr.passportease.repository.RequestOfficeRepository;
import it.univr.passportease.repository.RequestRepository;
import it.univr.passportease.repository.RequestTypeRepository;
import it.univr.passportease.repository.WorkerRepository;
import it.univr.passportease.service.jwt.JwtService;
import lombok.AllArgsConstructor;

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
            Integer totalNumberOfWorker = workerRepository.countByOffice(office);
            // TODO: delete this
            System.out.println("totalNumberOfWorker: " + totalNumberOfWorker);
            Integer busyWorkers = requestRepository.countBusyWorkersByOfficeAndDataRange(office,
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

    // TODO:
    private void createAvailabilities(Date startDate, Date endDate, List<Office> offices, Request request) {
        /*
         * status
         * date
         * time
         * request_id
         * office_id
         * user_id
         */
        
    }

}
