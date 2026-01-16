package com.vkteenvan.ratelimiter.policies;

import com.vkteenvan.ratelimiter.core.PolicyType;
import com.vkteenvan.ratelimiter.core.RateLimitDecision;
import com.vkteenvan.ratelimiter.core.RateLimitPolicy;
import com.vkteenvan.ratelimiter.core.RejectionReason;
import com.vkteenvan.ratelimiter.core.RequestContext;
import com.vkteenvan.ratelimiter.storage.FixedWindowState;
import com.vkteenvan.ratelimiter.storage.StateStore;

public final class FixedWindowPolicy implements RateLimitPolicy {
    private final String policyId;
    private final int limit;
    private final long windowSeconds;

    public FixedWindowPolicy(String policyId, int limit, long windowSeconds) {
        this.policyId = policyId;
        this.limit = limit;
        this.windowSeconds = windowSeconds;
    }

    @Override
    public RateLimitDecision evaluate(RequestContext context, StateStore store) {
        FixedWindowState state = store.getFixedWindowState(context.clientId(), policyId);
        long nowEpochSeconds = context.timestamp().getEpochSecond();
        boolean allowed = state.tryConsume(nowEpochSeconds, limit, windowSeconds);
        if (allowed) {
            return RateLimitDecision.allow();
        }
        long retryAfter = state.retryAfterSeconds(nowEpochSeconds, windowSeconds);
        return RateLimitDecision.reject(RejectionReason.FIXED_WINDOW_LIMIT_EXCEEDED, retryAfter);
    }

    @Override
    public PolicyType type() {
        return PolicyType.FIXED_WINDOW;
    }
}
