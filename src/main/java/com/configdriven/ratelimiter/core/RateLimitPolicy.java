package com.configdriven.ratelimiter.core;

import com.configdriven.ratelimiter.storage.StateStore;

public interface RateLimitPolicy {
    RateLimitDecision evaluate(RequestContext context, StateStore store);

    PolicyType type();
}
