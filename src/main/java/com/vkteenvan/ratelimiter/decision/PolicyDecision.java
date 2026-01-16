package com.vkteenvan.ratelimiter.decision;

import java.time.Duration;
import java.util.Optional;

/**
 * Represents the outcome of evaluating a single rate limiting policy
 * for a request.
 *
 * A PolicyDecision captures only policy-local information and does not
 * represent the final system decision. System-level aggregation is
 * handled separately.
 *
 * Instances are immutable and safe to share across threads.
 */

public class PolicyDecision {

    public enum Status {
        ALLOW,
        REJECT
    }

    private final Status status;
    private final Optional<Duration> retryAfter;
    private final Optional<Long> remainingQuota;

    private PolicyDecision(
            Status status,
            Optional<Duration> retryAfter,
            Optional<Long> remainingQuota
    ) {
        this.status = status;
        this.retryAfter = retryAfter;
        this.remainingQuota = remainingQuota;
    }

    public static PolicyDecision allow(Optional<Long> remainingQuota) {
        return new PolicyDecision(Status.ALLOW, Optional.empty(), remainingQuota);
    }

    public static PolicyDecision reject(Optional<Duration> retryAfter) {
        return new PolicyDecision(Status.REJECT, retryAfter, Optional.empty());
    }

    public Status status() {
        return status;
    }

    public Optional<Duration> retryAfter() {
        return retryAfter;
    }

    public Optional<Long> remainingQuota() {
        return remainingQuota;
    }

}
