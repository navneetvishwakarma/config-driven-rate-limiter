package com.configdriven.ratelimiter.service;

import com.configdriven.ratelimiter.config.PolicyConfig;
import com.configdriven.ratelimiter.core.PolicyType;
import com.configdriven.ratelimiter.core.RateLimitPolicy;
import com.configdriven.ratelimiter.policies.FixedWindowPolicy;
import com.configdriven.ratelimiter.policies.TokenBucketPolicy;

public final class PolicyFactory {
    private PolicyFactory() {}

    public static RateLimitPolicy create(PolicyConfig config, String policyId) {
        PolicyType type = require(config.type(), "type");
        return switch (type) {
            case FIXED_WINDOW -> new FixedWindowPolicy(
                    policyId,
                    require(config.limit(), "limit"),
                    require(config.windowSeconds(), "windowSeconds")
            );
            case TOKEN_BUCKET -> new TokenBucketPolicy(
                    policyId,
                    require(config.capacity(), "capacity"),
                    require(config.refillRatePerSecond(), "refillRatePerSecond")
            );
        };
    }

    private static <T> T require(T value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("Missing required policy field: " + field);
        }
        return value;
    }
}
