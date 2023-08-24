package it.univr.passportease.helper.ratelimiter;

import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BucketLimiter {
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String methodName) {
        return bucketCache.computeIfAbsent(methodName, k -> Bucket.builder()
                .addLimit(RateLimiter.valueOf(k).getLimit())
                .build());
    }

    public String getMethodName() {
        // return the name of the method that called this method
        return Thread
                .currentThread()
                .getStackTrace()[2]
                .getMethodName();
    }
}
