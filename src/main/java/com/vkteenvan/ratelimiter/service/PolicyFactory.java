package com.vkteenvan.ratelimiter.service;

import com.vkteenvan.ratelimiter.config.PolicyConfig;
import com.vkteenvan.ratelimiter.core.PolicyType;
import com.vkteenvan.ratelimiter.policy.FixedWindowPolicy;
import com.vkteenvan.ratelimiter.policy.IPolicy;
import com.vkteenvan.ratelimiter.policy.TokenBucketPolicy;

import java.time.Clock;
import java.util.Map;

public final class PolicyFactory {
    private PolicyFactory() {}

    public static IPolicy create(PolicyConfig config, String policyId) {
        PolicyType type = require(config.type(), "type");
        Map<String, Object> parameters = require(config.parameters(), "parameters");
        return switch (type) {
            case FIXED_WINDOW -> new FixedWindowPolicy(
                    policyId,
                    requirePositiveInt(parameters, "limit"),
                    requirePositiveInt(parameters, "windowSeconds"),
                    Clock.systemUTC()
            );
            case TOKEN_BUCKET -> new TokenBucketPolicy(
                    policyId,
                    requirePositiveLong(parameters, "capacity"),
                    requireNonNegativeDouble(parameters, "refillRatePerSecond"),
                    Clock.systemUTC()
            );
        };
    }

    private static <T> T require(T value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("Missing required policy field: " + field);
        }
        return value;
    }

    private static int requirePositiveInt(Map<String, Object> parameters, String field) {
        int value = requireNumber(parameters, field).intValue();
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be > 0");
        }
        return value;
    }

    private static long requirePositiveLong(Map<String, Object> parameters, String field) {
        long value = requireNumber(parameters, field).longValue();
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be > 0");
        }
        return value;
    }

    private static double requireNonNegativeDouble(Map<String, Object> parameters, String field) {
        double value = requireNumber(parameters, field).doubleValue();
        if (value < 0) {
            throw new IllegalArgumentException(field + " must be >= 0");
        }
        return value;
    }

    private static Number requireNumber(Map<String, Object> parameters, String field) {
        Object value = require(parameters.get(field), field);
        if (value instanceof Number number) {
            return number;
        }
        throw new IllegalArgumentException("Expected numeric policy field: " + field);
    }
}
