package com.vkteenvan.ratelimiter.config;

import com.vkteenvan.ratelimiter.core.PolicyType;
import java.util.Map;

/**
 * Immutable configuration describing a single policy instance.
 *
 * PolicyConfig provides the minimal information required to construct
 * a Policy implementation, including its type and associated parameters.
 *
 * Validation of parameter semantics is the responsibility of the
 * policy factory or configuration loader.
 */
public final class PolicyConfig {
    
    private final String policyId;
    private final PolicyType type;
    private final Map<String, Object> parameters;

    public PolicyConfig(String policyId, PolicyType type, Map<String, Object> parameters) {
        this.policyId = policyId;
        this.type = type;
        this.parameters = Map.copyOf(parameters);
    }

    public String policyId() {
        return policyId;
    }

    public PolicyType type() {
        return type;
    }

    public Map<String, Object> parameters() {
        return parameters;
    }

}