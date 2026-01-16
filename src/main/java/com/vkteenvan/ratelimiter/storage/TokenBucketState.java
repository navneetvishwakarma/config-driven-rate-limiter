package com.vkteenvan.ratelimiter.storage;

public final class TokenBucketState {
    private double tokens;
    private long lastRefillEpochMillis;

    public synchronized boolean tryConsume(
            long nowEpochMillis,
            long capacity,
            double refillRatePerSecond,
            double tokensRequested
    ) {
        refill(nowEpochMillis, capacity, refillRatePerSecond);
        if (tokens >= tokensRequested) {
            tokens -= tokensRequested;
            return true;
        }
        return false;
    }

    public synchronized long retryAfterMillis(
            long nowEpochMillis,
            long capacity,
            double refillRatePerSecond,
            double tokensRequested
    ) {
        refill(nowEpochMillis, capacity, refillRatePerSecond);
        if (tokens >= tokensRequested) {
            return 0;
        }
        double missing = tokensRequested - tokens;
        if (refillRatePerSecond <= 0) {
            return Long.MAX_VALUE;
        }
        return (long) Math.ceil((missing / refillRatePerSecond) * 1000);
    }

    private void refill(long nowEpochMillis, long capacity, double refillRatePerSecond) {
        if (lastRefillEpochMillis == 0) {
            lastRefillEpochMillis = nowEpochMillis;
            tokens = capacity;
            return;
        }
        double elapsedSeconds = (nowEpochMillis - lastRefillEpochMillis) / 1000.0;
        if (elapsedSeconds <= 0) {
            return;
        }
        tokens = Math.min(capacity, tokens + (elapsedSeconds * refillRatePerSecond));
        lastRefillEpochMillis = nowEpochMillis;
    }
}
