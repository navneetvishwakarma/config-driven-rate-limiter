package com.vkteenvan.ratelimiter.core;

import com.vkteenvan.ratelimiter.storage.StateStore;

public interface RateLimitPolicy {
    RateLimitDecision evaluate(RequestContext context, StateStore store);

    PolicyType type();
}
