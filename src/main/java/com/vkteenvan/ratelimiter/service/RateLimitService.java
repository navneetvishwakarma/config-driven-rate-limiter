package com.vkteenvan.ratelimiter.service;

import com.vkteenvan.ratelimiter.config.ClientConfig;
import com.vkteenvan.ratelimiter.config.PolicyConfig;
import com.vkteenvan.ratelimiter.core.RateLimitDecision;
import com.vkteenvan.ratelimiter.core.RateLimitPolicy;
import com.vkteenvan.ratelimiter.core.RequestContext;
import com.vkteenvan.ratelimiter.storage.StateStore;

import java.time.Instant;
import java.util.List;

public final class RateLimitService {
    private final StateStore store;

    public RateLimitService(StateStore store) {
        this.store = store;
    }

    public RateLimitDecision evaluate(ClientConfig config, Instant timestamp) {
        RequestContext context = new RequestContext(config.clientId(), timestamp);
        List<PolicyConfig> policies = config.policies();
        for (int index = 0; index < policies.size(); index++) {
            PolicyConfig policyConfig = policies.get(index);
            String policyId = policyConfig.id() != null ? policyConfig.id() : "policy-" + index;
            RateLimitPolicy policy = PolicyFactory.create(policyConfig, policyId);
            RateLimitDecision decision = policy.evaluate(context, store);
            if (!decision.allowed()) {
                return decision;
            }
        }
        return RateLimitDecision.allow();
    }
}
