package it.univr.passportease.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.notfound.UserOrWorkerIDNotFoundException;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.helper.Roles;
import it.univr.passportease.helper.UserType;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.repository.WorkerRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

/**
 * This class is used to generate and validate JWT tokens. It manages both access and refresh tokens.
 */
@Service
@AllArgsConstructor
public class JwtService {
    /**
     * The access token key used to sign the access token.
     */
    @NonNull
    private static final String ACCESS_TOKEN_KEY = System.getenv("ACCESS_KEY");
    /**
     * The refresh token key used to sign the refresh token.
     */
    @NonNull
    private static final String REFRESH_TOKEN_KEY = System.getenv("REFRESH_KEY");

    /**
     * The repository for the {@link Worker} entity.
     */
    private final WorkerRepository workerRepository;
    /**
     * The repository for the {@link User} entity.
     */
    private final UserRepository userRepository;
    /**
     * The redis template used to store the access tokens.
     */
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Extracts the id of the user or worker from the token.
     *
     * @param token JWT token
     * @return the id of the user or worker that owns the token
     */
    public UUID extractId(JWT token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

    /**
     * Extracts the expiration date of the token.
     *
     * @param token JWT token
     * @return the expiration date of the token
     */
    public Date extractExpiration(JWT token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the claims of the token.
     *
     * @param token          JWT token
     * @param claimsResolver function that takes a Claims object and returns a T object
     * @param <T>            type of the object to return
     * @return the object of type T extracted from the token
     */
    public <T> T extractClaim(JWT token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all the claims of the token.
     *
     * @param token JWT token
     * @return all the claims of the token
     */
    private Claims extractAllClaims(JWT token) {
        return Jwts.parser()
                .verifyWith(getAccessSignKey())
                .build()
                .parseSignedClaims(token.getToken())
                .getPayload();
    }

    /**
     * A token is expired if:
     * <ul>
     *     <li>it is expired</li>
     *     <li>it is not in redis</li>
     *     <li>the token in redis is not the same as the token in the request</li>
     *     <li>the token has nbf (not before) field and if it is after the current time</li>
     *     <li>the token has iat (issued at) field and if it is after the current time</li>
     * </ul>
     *
     * @param token JWT token
     * @return true if the token is expired, false otherwise.
     */
    public Boolean isTokenExpired(JWT token) {
        /*
         * Checks for understanding if the token is expired:
         * 1. if the token is expired
         * 2. if the token is not in redis
         * 3. if the token in redis is not the same as the token in the request
         * 4. if the token has nbf (not before) field and if it is after the current time
         * 5. if the token has iat (issued at) field and if it is after the current time
         */
        return extractExpiration(token).before(new Date()) &&
                redisTemplate.opsForValue().get(extractId(token).toString()) == null &&
                !Objects.equals(redisTemplate.opsForValue().get(extractId(token).toString()), token.getToken()) &&
                extractAllClaims(token).get("nbf") != null &&
                extractAllClaims(token).get("nbf", Date.class).after(new Date()) &&
                extractAllClaims(token).get("iat") != null &&
                extractAllClaims(token).get("iat", Date.class).after(new Date());
    }

    /**
     * Checks if the token is valid.
     *
     * @param token       JWT token
     * @param userDetails user details of the user that owns the token
     * @return true if the token is valid, false otherwise
     */
    public Boolean validTokenFromUserDetails(JWT token, UserDetails userDetails) {
        final String id = extractId(token).toString();

        JWT tokenInRedis = new JWT(Objects.requireNonNull(redisTemplate.opsForValue().get(id)));

        return id.equals(userDetails.getUsername()) &&
                !isTokenExpired(token) &&
                (tokenInRedis.equals(token));
    }

    /**
     * Generates a new access token.
     *
     * @param id id of the user or worker
     * @return a new access token, valid for 15 minutes
     * @throws UserOrWorkerIDNotFoundException if the id does not belong to either a user or a worker
     */
    public JWT generateAccessToken(UUID id) throws UserOrWorkerIDNotFoundException {
        Map<String, Object> claims = new HashMap<>();
        return createAccessToken(claims, id);
    }

    /**
     * Generates a new access token.
     *
     * @param claims claims to add to the token
     * @param id     id of the user or worker
     * @return a new access token, valid for 15 minutes
     * @throws UserOrWorkerIDNotFoundException if the id does not belong to either a user or a worker
     */
    private JWT createAccessToken(Map<String, Object> claims, UUID id) throws UserOrWorkerIDNotFoundException {
        claims.put("role", getRoleById(id).toString());
        claims.put("typ", "JWT");

        Date now = new Date(System.currentTimeMillis());
        Date exp15Minutes = new Date(System.currentTimeMillis() + 1000 * 60 * 15);

        JWT accessToken = new JWT(Jwts.builder()
                .signWith(getAccessSignKey())
                .claims().add(claims)
                .subject(id.toString())
                .issuedAt(now)
                .expiration(exp15Minutes)
                .notBefore(now)
                .id(UUID.randomUUID().toString())
                .and().compact()
        );


        saveTokenInRedis(id, accessToken);
        return accessToken;
    }

    /**
     * Saves the token in redis with the key being the id of the user or worker.
     *
     * @param id    id of the user or worker
     * @param token JWT token
     */
    private void saveTokenInRedis(UUID id, JWT token) {
        redisTemplate.opsForValue().set(id.toString(), token.getToken());
    }

    /**
     * Generates a new refresh token.
     *
     * @param id id of the user or worker
     * @return a new refresh token, valid for 30 days
     */
    public JWT generateRefreshToken(UUID id) {
        return createRefreshToken(id);
    }

    /**
     * Generates a new refresh token.
     *
     * @param id id of the user or worker
     * @return a new refresh token, valid for 30 days
     */
    private JWT createRefreshToken(UUID id) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillisFor30Days = nowMillis + 1000L * 60 * 60 * 24 * 30; // 30 days
        Date fromNow30Days = new Date(expMillisFor30Days);

        JWT refreshtoken = new JWT(Jwts.builder()
                .signWith(getRefreshSignKey())
                .claims().add("typ", "JWT")
                .issuedAt(now)
                .expiration(fromNow30Days)
                .id(UUID.randomUUID().toString())
                .subject(id.toString())
                .and().compact()
        );

        saveRefreshTokenInDB(id, refreshtoken);

        return refreshtoken;
    }

    /**
     * Saves the refresh token in the database.
     *
     * @param id           id of the user or worker
     * @param refreshtoken JWT refresh token
     */
    private void saveRefreshTokenInDB(UUID id, JWT refreshtoken) {
        Optional<User> user = userRepository.findById(id);
        Optional<Worker> worker = workerRepository.findById(id);

        if (user.isPresent()) {
            user.get().setRefreshToken(refreshtoken.getToken());
            userRepository.save(user.get());
        } else if (worker.isPresent()) {
            worker.get().setRefreshToken(refreshtoken.getToken());
            workerRepository.save(worker.get());
        }
    }

    /**
     * Gets the role of the user or worker.
     *
     * @param id id of the user or worker
     * @return an {@link Roles} object representing the role of the user or worker
     * @throws UserOrWorkerIDNotFoundException if the id does not belong to either a user or a worker
     */
    private Roles getRoleById(UUID id) throws UserOrWorkerIDNotFoundException {
        if (workerRepository.findById(id).isPresent()) return Roles.WORKER;
        else if (userRepository.findById(id).isPresent()) return Roles.USER;
        throw new UserOrWorkerIDNotFoundException("ID does not belong to either Worker or User");
    }

    /**
     * Gets the {@link SecretKey} used to sign the access token.
     *
     * @return the {@link SecretKey} used to sign the access token
     */
    private SecretKey getAccessSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ACCESS_TOKEN_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gets the {@link SecretKey} used to sign the refresh token.
     *
     * @return the {@link SecretKey} used to sign the refresh token
     */
    private SecretKey getRefreshSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(REFRESH_TOKEN_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Invalidates the access token by deleting it from redis.
     *
     * @param token JWT Access token
     * @return true if the access token is deleted from redis, false otherwise
     */
    public Boolean invalidateAccessToken(JWT token) {
        // TODO: throw exception if token was not deleted
        return Boolean.TRUE.equals(redisTemplate.delete(extractId(token).toString()));
    }

    /**
     * Invalidates the refresh token by setting it to an empty string.
     *
     * @param token JWT Refresh token
     * @throws UserNotFoundException if the user or worker is not found
     */
    public void invalidateRefreshToken(JWT token) throws UserNotFoundException {
        Object userOrWorker = getUserOrWorkerFromToken(token);
        if (!(userOrWorker instanceof User) && !(userOrWorker instanceof Worker))
            throw new UserNotFoundException("Invalid User or Worker ID");

        if (userOrWorker instanceof User user) {
            user.setRefreshToken("");
            userRepository.save(user);
            return;
        }

        ((Worker) userOrWorker).setRefreshToken("");
        workerRepository.save((Worker) userOrWorker);
    }

    /**
     * Wrapper function to return User or Worker depending on the token.
     *
     * @param token JWT token
     * @return the user or worker that owns the token, as a {@link UserType} object
     * @throws UserNotFoundException if the user or worker is not found
     */
    public UserType getUserOrWorkerFromToken(JWT token) throws UserNotFoundException {
        UUID id = extractId(token);
        Optional<User> user = userRepository.findById(id);
        Optional<Worker> worker = workerRepository.findById(id);

        if (user.isPresent())
            return user.get();
        else if (worker.isPresent())
            return worker.get();
        throw new UserNotFoundException("Invalid User or Worker ID");
    }
}
