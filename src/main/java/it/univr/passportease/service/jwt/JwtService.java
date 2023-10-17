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

@Service
@AllArgsConstructor
public class JwtService {
    @NonNull
    private static final String ACCESS_TOKEN_KEY = System.getenv("ACCESS_KEY");
    @NonNull
    private static final String REFRESH_TOKEN_KEY = System.getenv("REFRESH_KEY");

    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private RedisTemplate<String, String> redisTemplate;

    public UUID extractId(JWT token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

    public Date extractExpiration(JWT token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(JWT token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(JWT token) {
        return Jwts.parser()
                .verifyWith(getAccessSignKey())
                .build()
                .parseSignedClaims(token.getToken())
                .getPayload();
    }

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

    public Boolean validateToken(JWT token, UserDetails userDetails) {
        final String id = extractId(token).toString();

        JWT tokenInRedis = new JWT(Objects.requireNonNull(redisTemplate.opsForValue().get(id)));

        return id.equals(userDetails.getUsername()) &&
                !isTokenExpired(token) &&
                (tokenInRedis.equals(token));
    }

    public JWT generateAccessToken(UUID id) throws UserOrWorkerIDNotFoundException {
        Map<String, Object> claims = new HashMap<>();
        return createAccessToken(claims, id);
    }

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

    private void saveTokenInRedis(UUID id, JWT token) {
        redisTemplate.opsForValue().set(id.toString(), token.getToken());
    }

    public JWT generateRefreshToken(UUID id) {
        return createRefreshToken(id);
    }

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

    private Roles getRoleById(UUID id) throws UserOrWorkerIDNotFoundException {
        if (workerRepository.findById(id).isPresent()) return Roles.WORKER;
        else if (userRepository.findById(id).isPresent()) return Roles.USER;
        throw new UserOrWorkerIDNotFoundException("ID does not belong to either Worker or User");
    }

    private SecretKey getAccessSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ACCESS_TOKEN_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getRefreshSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(REFRESH_TOKEN_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean invalidateAccessToken(JWT token) {
        return Boolean.TRUE.equals(redisTemplate.delete(extractId(token).toString()));
    }

    public void invalidateRefreshToken(JWT token) throws UserNotFoundException {
        Object userOrWorker = getUserOrWorkerFromToken(token);
        if (userOrWorker instanceof User user) {
            user.setRefreshToken("");
            userRepository.save(user);
        } else if (userOrWorker instanceof Worker worker) {
            worker.setRefreshToken("");
            workerRepository.save(worker);
        }
    }

    // wrapper function to return User or Worker depending on the token
    public Object getUserOrWorkerFromToken(JWT token) throws UserNotFoundException {
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
