package it.univr.passportease.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.univr.passportease.entity.User;
import it.univr.passportease.entity.Worker;
import it.univr.passportease.exception.notfound.UserNotFoundException;
import it.univr.passportease.exception.notfound.UserOrWorkerIDNotFoundException;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.repository.WorkerRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class JwtService {
    // TODO: eliminare
    private static final String KEY = "FrancescoTiAmoPeroQuestaPasswordETroppoCortaQuindiDevoAllungarlaUnPoDiPiuAliceCiao";
    @Value("${jwt.secret}")
    private static String accessKey;
    @Value("${refreshtoken.secret}")
    private static String refreshKey;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private RedisTemplate<String, String> redisTemplate;

    public UUID extractId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return (Claims) Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parse(token)
                .getPayload();
    }

    public Boolean isTokenExpired(String token) {
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
                !Objects.equals(redisTemplate.opsForValue().get(extractId(token).toString()), token) &&
                extractAllClaims(token).get("nbf") != null &&
                extractAllClaims(token).get("nbf", Date.class).after(new Date()) &&
                extractAllClaims(token).get("iat") != null &&
                extractAllClaims(token).get("iat", Date.class).after(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String id = extractId(token).toString();

        if (redisTemplate.opsForValue().get(id) == null) return false;

        @NonNull String tokenInRedis = Objects.requireNonNull(redisTemplate.opsForValue().get(id));
        return id.equals(userDetails.getUsername()) &&
                !isTokenExpired(token) &&
                (tokenInRedis.equals(token));
    }

    public String generateAccessToken(UUID id) throws UserOrWorkerIDNotFoundException {
        Map<String, Object> claims = new HashMap<>();
        return createAccessToken(claims, id);
    }

    private String createAccessToken(Map<String, Object> claims, UUID id) throws UserOrWorkerIDNotFoundException {
        claims.put("role", getRoleById(id));
        claims.put("typ", "JWT");

        Date now = new Date(System.currentTimeMillis());
        Date exp15Minutes = new Date(System.currentTimeMillis() + 1000 * 60 * 15);

        String accessToken = Jwts.builder()
                .signWith(getSignKey())
                .claims().add(claims)
                .subject(id.toString())
                .issuedAt(now)
                .expiration(exp15Minutes)
                .notBefore(now)
                .id(UUID.randomUUID().toString())
                .and().compact();


        saveTokenInRedis(id, accessToken);
        return accessToken;
    }

    private void saveTokenInRedis(UUID id, String token) {
        redisTemplate.opsForValue().set(id.toString(), token);
    }

    public String generateRefreshToken(UUID id) {
        return createRefreshToken(id);
    }

    private String createRefreshToken(UUID id) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillisFor30Days = nowMillis + 1000L * 60 * 60 * 24 * 30; // 30 days
        Date fromNow30Days = new Date(expMillisFor30Days);

        String refreshtoken = Jwts.builder()
                .signWith(getSignKey())
                .claims().add("typ", "JWT")
                .issuedAt(now)
                .expiration(fromNow30Days)
                .id(UUID.randomUUID().toString())
                .subject(id.toString())
                .and().compact();

        saveRefreshTokenInDB(id, refreshtoken);

        return refreshtoken;
    }

    private void saveRefreshTokenInDB(UUID id, String refreshtoken) {
        Optional<User> user = userRepository.findById(id);
        Optional<Worker> worker = workerRepository.findById(id);

        if (user.isPresent()) {
            user.get().setRefreshToken(refreshtoken);
            userRepository.save(user.get());
        } else if (worker.isPresent()) {
            worker.get().setRefreshToken(refreshtoken);
            workerRepository.save(worker.get());
        }
    }

    private String getRoleById(UUID id) throws UserOrWorkerIDNotFoundException {
        if (workerRepository.findById(id).isPresent()) {
            return "worker";
        } else if (userRepository.findById(id).isPresent()) {
            return "user";
        } else {
            throw new UserOrWorkerIDNotFoundException("ID does not belong to either Worker or User");
        }
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean invalidateAccessToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.delete(extractId(token).toString()));
    }

    public void invalidateRefreshToken(String token) throws UserNotFoundException {
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
    public Object getUserOrWorkerFromToken(String token) throws UserNotFoundException {
        UUID id = extractId(token);
        Optional<User> user = userRepository.findById(id);
        Optional<Worker> worker = workerRepository.findById(id);

        if (user.isPresent())
            return user.get();
        else if (worker.isPresent())
            return worker.get();
        else
            throw new UserNotFoundException("Invalid User and Worker ID");
    }
}
