package com.vkteenvan.ratelimiter.service;

import com.vkteenvan.ratelimiter.config.PolicyConfig;
import com.vkteenvan.ratelimiter.core.PolicyType;
import com.vkteenvan.ratelimiter.core.RateLimitPolicy;
import com.vkteenvan.ratelimiter.policies.FixedWindowPolicy;
import com.vkteenvan.ratelimiter.policies.TokenBucketPolicy;

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
