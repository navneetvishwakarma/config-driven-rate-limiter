package com.vkteenvan.ratelimiter.policy;

import com.vkteenvan.ratelimiter.decision.PolicyDecision;
import com.vkteenvan.ratelimiter.storage.TokenBucketState;

import java.time.Clock;
import java.time.Duration;
import java.util.Optional;

public final class TokenBucketPolicy implements IPolicy {
    private final String policyId;
    private final long capacity;
    private final double refillRatePerSecond;
    private final Clock clock;
    private final TokenBucketState state = new TokenBucketState();

    public TokenBucketPolicy(
            String policyId,
            long capacity,
            double refillRatePerSecond,
            Clock clock
    ) {
        this.policyId = policyId;
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.clock = clock;
    }

    @Override
    public PolicyDecision evaluate() {
        long nowMillis = clock.millis();
        boolean allowed = state.tryConsume(nowMillis, capacity, refillRatePerSecond, 1);
        if (allowed) {
            return PolicyDecision.allow(Optional.empty());
        }

        long retryAfterMillis = state.retryAfterMillis(nowMillis, capacity, refillRatePerSecond, 1);
        Optional<Duration> retryAfter = retryAfterMillis == Long.MAX_VALUE
                ? Optional.empty()
                : Optional.of(Duration.ofMillis(retryAfterMillis));
        return PolicyDecision.reject(retryAfter);
    }

    @Override
    public String policyId() {
        return policyId;
    }
}
