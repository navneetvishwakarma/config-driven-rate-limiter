package com.configdriven.ratelimiter.core;

public record RateLimitDecision(boolean allowed, RejectionReason reason, Long retryAfterSeconds) {
    public static RateLimitDecision allow() {
        return new RateLimitDecision(true, null, null);
    }

    public static RateLimitDecision reject(RejectionReason reason, long retryAfterSeconds) {
        return new RateLimitDecision(false, reason, retryAfterSeconds);
    }
}
