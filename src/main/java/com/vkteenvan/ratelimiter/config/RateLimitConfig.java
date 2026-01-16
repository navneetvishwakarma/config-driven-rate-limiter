package com.vkteenvan.ratelimiter.config;

import java.util.List;

/**
 * Immutable configuration describing rate limiting behavior for a client.
 *
 * A RateLimitConfig defines the set of policies that apply to a client
 * and the order in which they are evaluated. Configuration is loaded
 * during initialization and remains immutable for the lifetime of
 * the process.
 */
public final class RateLimitConfig {
    private final String clientId;
    private final List<PolicyConfig> policies;

    public RateLimitConfig(String clientId, List<PolicyConfig> policies) {
        this.clientId = clientId;
        this.policies = List.copyOf(policies);
    }

    public String clientId() {
        return clientId;
    }

    public List<PolicyConfig> policies() {
        return policies;
    }
}
