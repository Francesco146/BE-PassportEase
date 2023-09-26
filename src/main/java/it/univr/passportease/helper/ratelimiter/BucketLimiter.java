package it.univr.passportease.helper.ratelimiter;

import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BucketLimiter {
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(RateLimiter methodName) {
        return bucketCache.computeIfAbsent(methodName.name(), k -> Bucket.builder()
                .addLimit(RateLimiter.valueOf(k).getLimit())
                .build());
    }
}
