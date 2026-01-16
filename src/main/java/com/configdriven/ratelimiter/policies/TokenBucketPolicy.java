package com.configdriven.ratelimiter.policies;

import com.configdriven.ratelimiter.core.PolicyType;
import com.configdriven.ratelimiter.core.RateLimitDecision;
import com.configdriven.ratelimiter.core.RateLimitPolicy;
import com.configdriven.ratelimiter.core.RejectionReason;
import com.configdriven.ratelimiter.core.RequestContext;
import com.configdriven.ratelimiter.storage.StateStore;
import com.configdriven.ratelimiter.storage.TokenBucketState;

public final class TokenBucketPolicy implements RateLimitPolicy {
    private final String policyId;
    private final long capacity;
    private final double refillRatePerSecond;

    public TokenBucketPolicy(String policyId, long capacity, double refillRatePerSecond) {
        this.policyId = policyId;
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    @Override
    public RateLimitDecision evaluate(RequestContext context, StateStore store) {
        TokenBucketState state = store.getTokenBucketState(context.clientId(), policyId);
        long nowMillis = context.timestamp().toEpochMilli();
        boolean allowed = state.tryConsume(nowMillis, capacity, refillRatePerSecond, 1);
        if (allowed) {
            return RateLimitDecision.allow();
        }
        long retryAfterMillis = state.retryAfterMillis(nowMillis, capacity, refillRatePerSecond, 1);
        long retryAfterSeconds = Math.max(0, (long) Math.ceil(retryAfterMillis / 1000.0));
        return RateLimitDecision.reject(RejectionReason.TOKEN_BUCKET_EMPTY, retryAfterSeconds);
    }

    @Override
    public PolicyType type() {
        return PolicyType.TOKEN_BUCKET;
    }
}
