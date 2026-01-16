package com.vkteenvan.ratelimiter.storage;

public final class FixedWindowState {
    private long windowStartEpochSeconds;
    private int count;

    public synchronized boolean tryConsume(long nowEpochSeconds, int limit, long windowSeconds) {
        if (windowStartEpochSeconds == 0
                || nowEpochSeconds - windowStartEpochSeconds >= windowSeconds) {
            windowStartEpochSeconds = nowEpochSeconds;
            count = 0;
        }
        if (count < limit) {
            count++;
            return true;
        }
        return false;
    }

    public synchronized long retryAfterSeconds(long nowEpochSeconds, long windowSeconds) {
        if (windowStartEpochSeconds == 0) {
            return 0;
        }
        long windowEnd = windowStartEpochSeconds + windowSeconds;
        return Math.max(0, windowEnd - nowEpochSeconds);
    }
}
