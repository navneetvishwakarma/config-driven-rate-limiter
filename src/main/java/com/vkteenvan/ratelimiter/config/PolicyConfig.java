package com.vkteenvan.ratelimiter.config;

import com.vkteenvan.ratelimiter.core.PolicyType;

public record PolicyConfig(
        String id,
        PolicyType type,
        Integer limit,
        Integer windowSeconds,
        Integer capacity,
        Double refillRatePerSecond
) {}
