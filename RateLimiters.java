import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class RateLimiters {

    public interface RateLimiter {
        boolean tryAcquire();
    }

    // ---------------------------------------------------------- Token bucket
    public static class TokenBucketRateLimiter implements RateLimiter {
        private final long capacity;
        private final double refillRate;   // tokens per nanosecond
        private double tokens;
        private long lastRefillTime;

        public TokenBucketRateLimiter(long capacity, double refillPerSec) {
            this.capacity = capacity;
            this.refillRate = refillPerSec / 1_000_000_000;
            this.tokens = capacity;
            this.lastRefillTime = System.nanoTime();
        }

        @Override
        public synchronized boolean tryAcquire() {
            refill();
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            tokens = Math.min(capacity, tokens + (now - lastRefillTime) * refillRate);
            lastRefillTime = now;
        }
    }

    // -------------------------------------------------------- Sliding window
    public static class SlidingWindowRateLimiter implements RateLimiter {
        private final int maxRequests;
        private final long windowNanos;
        private final Deque<Long> timestamps = new ArrayDeque<>();

        public SlidingWindowRateLimiter(int maxRequests, long windowMillis) {
            this.maxRequests = maxRequests;
            this.windowNanos = windowMillis * 1_000_000L;
        }

        @Override
        public synchronized boolean tryAcquire() {
            long now = System.nanoTime();
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() >= windowNanos) {
                timestamps.pollFirst();
            }

            if (timestamps.size() < maxRequests) {
                timestamps.addLast(now);
                return true;
            }
            return false;
        }
    }

    // ------------------------------------------------------------- Per key
    public static class PerKeyRateLimiter<K> {
        private final ConcurrentMap<K, RateLimiter> limiters = new ConcurrentHashMap<>();
        private final Supplier<RateLimiter> factory;

        public PerKeyRateLimiter(Supplier<RateLimiter> factory) {
            this.factory = factory;
        }

        public boolean tryAcquire(K key) {
            return limiters.computeIfAbsent(key, k -> factory.get()).tryAcquire();
        }
    }

    // ---------------------------------------------------------- Quick test
    public static void main(String[] args) {
        RateLimiter limiter = new TokenBucketRateLimiter(3, 1);
        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " + limiter.tryAcquire());
        }
        // Expected: true, true, true, false, false
    }
}