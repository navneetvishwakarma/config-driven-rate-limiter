package com.configdriven.ratelimiter.config;

import com.configdriven.ratelimiter.core.PolicyType;

public record PolicyConfig(
        String id,
        PolicyType type,
        Integer limit,
        Integer windowSeconds,
        Integer capacity,
        Double refillRatePerSecond
) {}
