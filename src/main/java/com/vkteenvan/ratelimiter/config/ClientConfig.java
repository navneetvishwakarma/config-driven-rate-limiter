package com.vkteenvan.ratelimiter.config;

import java.util.List;

public record ClientConfig(String clientId, List<PolicyConfig> policies) {}
