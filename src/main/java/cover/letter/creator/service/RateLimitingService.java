package cover.letter.creator.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    // Cache buckets by IP / User identifier
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> forgotPasswordBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> aiGenerationBuckets = new ConcurrentHashMap<>();

    /**
     * Rate limit for Login: 5 attempts per 1 minute per client IP
     */
    public boolean allowLogin(String clientIp) {
        Bucket bucket = loginBuckets.computeIfAbsent(clientIp, k -> createBucket(5, Duration.ofMinutes(1)));
        return bucket.tryConsume(1);
    }

    /**
     * Rate limit for Registration: 3 accounts per 10 minutes per client IP
     */
    public boolean allowRegister(String clientIp) {
        Bucket bucket = registerBuckets.computeIfAbsent(clientIp, k -> createBucket(3, Duration.ofMinutes(10)));
        return bucket.tryConsume(1);
    }

    /**
     * Rate limit for Forgot Password: 3 requests per 10 minutes per client IP
     */
    public boolean allowForgotPassword(String clientIp) {
        Bucket bucket = forgotPasswordBuckets.computeIfAbsent(clientIp, k -> createBucket(3, Duration.ofMinutes(10)));
        return bucket.tryConsume(1);
    }

    /**
     * Rate limit for AI CV Generation: 5 generations per 1 minute per client (User / IP)
     */
    public boolean allowAiGeneration(String clientIdentifier) {
        Bucket bucket = aiGenerationBuckets.computeIfAbsent(clientIdentifier, k -> createBucket(5, Duration.ofMinutes(1)));
        return bucket.tryConsume(1);
    }

    private Bucket createBucket(long capacity, Duration duration) {
        Refill refill = Refill.greedy(capacity, duration);
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
