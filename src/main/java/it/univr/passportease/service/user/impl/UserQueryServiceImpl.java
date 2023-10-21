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

/**
 * Implementation of {@link UserQueryService}.
 * It provides an implementation for the GraphQL queries that can be executed by the user.
 */
@Service
@AllArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    /**
     * The repository for {@link User} entity.
     */
    private final UserRepository userRepository;
    /**
     * The repository for {@link Notification} entity.
     */
    private final NotificationRepository notificationRepository;
    /**
     * The repository for {@link Availability} entity.
     */
    private final ReservationRepository reservationRepository;
    /**
     * The repository for {@link RequestType} entity.
     */
    private final RequestTypeRepository requestTypeRepository;
    /**
     * The service that handles the JWT.
     */
    private final JwtService jwtService;

    /**
     * Get the user details from the JWT token.
     *
     * @param token JWT token
     * @return User details
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public User getUserDetails(JWT token) throws UserNotFoundException {
        UUID id = jwtService.extractId(token);
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) throw new UserNotFoundException("User not found");

        return user.get();
    }

    /**
     * Get the list of notifications of the user.
     *
     * @param token JWT token
     * @return List of notifications
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public List<Notification> getUserNotifications(JWT token) {
        UUID id = jwtService.extractId(token);
        return notificationRepository.findByUserId(id);
    }

    /**
     * Get the list of reservations of the user.
     *
     * @param token JWT token
     * @return List of reservations of the user
     */
    @Override
    @PreAuthorize("hasAuthority('USER') && hasAuthority('VALIDATED')")
    public List<Availability> getUserReservations(JWT token) {
        UUID id = jwtService.extractId(token);
        return reservationRepository.findByUserId(id);
    }

    /**
     * Get the report details of the availability with the given UUID.
     *
     * @param availabilityId UUID of the availability
     * @param token          JWT token
     * @return Report details of the availability with the given UUID
     * @throws SecurityException              if the user is not the owner of the availability,
     *                                        or if the token is from a worker
     * @throws InvalidAvailabilityIDException if the availability is not found
     */
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

    /**
     * Get the list of request types that the user can make. If the user has already made a request for the
     * "digitalizzazione passaporto" or "creazione passaporto", then all the other request types are returned. Otherwise, only
     * "digitalizzazione passaporto" and "creazione passaporto" are returned.
     *
     * @param token JWT token
     * @return List of request types that the user can make
     * @throws InvalidRequestTypeException if the request type is not found
     */
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
