package com.vkteenvan.ratelimiter.policy;

import com.vkteenvan.ratelimiter.decision.PolicyDecision;

public interface IPolicy {

    /**
     * Evaluates the policy for a single request.
     *
     * Implementations are expected to be thread-safe.
     *
     * @return policy-level decision
     */
    PolicyDecision evaluate();

    /**
     * @return unique identifier of this policy instance
     */
    String policyId();
}
