package it.univr.passportease.service.user.impl;

import it.univr.passportease.dto.output.ReportDetails;
import it.univr.passportease.entity.*;
import it.univr.passportease.entity.enums.Status;
import it.univr.passportease.exception.invalid.InvalidAvailabilityIDException;
import it.univr.passportease.exception.invalid.InvalidRequestTypeException;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.repository.NotificationRepository;
import it.univr.passportease.repository.RequestTypeRepository;
import it.univr.passportease.repository.ReservationRepository;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.user.UserQueryService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ReservationRepository reservationRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final JwtService jwtService;

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public User getUserDetails(JWT token) throws UserNotFoundException {
        UUID id = jwtService.extractId(token);
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) throw new UserNotFoundException("User not found");

        return user.get();
    }

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public List<Notification> getUserNotifications(JWT token) {
        UUID id = jwtService.extractId(token);
        return notificationRepository.findByUserId(id);
    }

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public List<Availability> getUserReservations(JWT token) {
        UUID id = jwtService.extractId(token);
        return reservationRepository.findByUserId(id);
    }

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public ReportDetails getReportDetailsByAvailabilityID(String availabilityId, JWT token)
            throws SecurityException, InvalidAvailabilityIDException {
        Object userToken = jwtService.getUserOrWorkerFromToken(token);
        if (!(userToken instanceof User))
            throw new SecurityException("Only user can access reports");

        Optional<Availability> optionalAvailability = reservationRepository.findById(UUID.fromString(availabilityId));

        if (optionalAvailability.isEmpty())
            throw new InvalidAvailabilityIDException("Invalid Availability ID");

        Availability availability = optionalAvailability.get();
        if (!availability.getStatus().equals(Status.TAKEN))
            throw new InvalidAvailabilityIDException("Can't get report of free availabilities");

        User user = availability.getUser();
        String fiscalCodeAvailability = user.getFiscalCode();

        if (!fiscalCodeAvailability.equals(((User) userToken).getFiscalCode()))
            throw new SecurityException("Only user whose record belongs to can access it");

        Request request = availability.getRequest();
        Office office = availability.getOffice();
        RequestType requestType = request.getRequestType();

        return new ReportDetails(
                fiscalCodeAvailability,
                user.getName(),
                user.getSurname(),
                user.getCityOfBirth(),
                user.getDateOfBirth(),
                availability.getDate(),
                request.getStartTime(),
                requestType.getName(),
                office.getName(),
                office.getAddress()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public List<RequestType> getRequestTypesByUser(JWT token) throws InvalidRequestTypeException {
        UUID userId = jwtService.extractId(token);
        List<Availability> availabilities = reservationRepository.findByUserId(userId);

        List<RequestType> requestTypesOfUser = availabilities
                .stream()
                .filter(availability -> availability.getStatus().equals(Status.TAKEN) && availability.getDate().before(new Date()))
                .map(availability -> availability.getRequest().getRequestType())
                .distinct()
                .toList();

        RequestType digitalizzazionePassaporto = requestTypeRepository.findByName("Digitalizzazione Passaporto")
                .orElseThrow(() -> new InvalidRequestTypeException("Digitalizzazione Passaporto not found"));

        RequestType creazionePassaporto = requestTypeRepository.findByName("Creazione Passaporto")
                .orElseThrow(() -> new InvalidRequestTypeException("Creazione Passaporto not found"));

        List<RequestType> allRequestTypes = requestTypeRepository.findAll();

        if (requestTypesOfUser.contains(digitalizzazionePassaporto) || requestTypesOfUser.contains(creazionePassaporto)) {
            return allRequestTypes
                    .stream()
                    .filter(requestType -> !requestType.getName().equals("Digitalizzazione Passaporto") && !requestType.getName().equals("Creazione Passaporto"))
                    .toList();
        } else return List.of(digitalizzazionePassaporto, creazionePassaporto);
    }


}
