package it.univr.passportease.helper.ratelimiter;

import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to resolve the bucket for each method. It uses a ConcurrentHashMap to store the buckets.
 * The algorithm used is called "Token Bucket". It goes as follows:
 * <ul>
 *     <li>Each bucket has a capacity and a refill rate.</li>
 *     <li>When a request arrives, the bucket is checked to see if it has enough tokens to satisfy the request.</li>
 *     <li>If it does, the request is served and the bucket is updated.</li>
 *     <li>If it doesn't, the request is rejected.</li>
 *     <li>Each bucket has a refill rate, which is the rate at which the bucket is refilled with tokens.</li>
 *     <li>Each bucket has a capacity, which is the maximum number of tokens it can hold.</li>
 * </ul>
 */
@Service
public class BucketLimiter {
    /**
     * The cache of the buckets.
     */
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    /**
     * This method is used to resolve the bucket for each method. It uses a ConcurrentHashMap to store the buckets.
     *
     * @param methodName The name of the method to resolve the bucket for.
     * @return The bucket for the given method.
     */
    public Bucket resolveBucket(RateLimiter methodName) {
        return bucketCache.computeIfAbsent(methodName.name(), call -> Bucket.builder()
                .addLimit(RateLimiter.valueOf(call).getLimit())
                .build()
        );
    }
}
