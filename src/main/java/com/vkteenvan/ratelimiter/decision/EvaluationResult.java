package com.vkteenvan.ratelimiter.decision;

import java.util.Optional;

/**
 * Represents the final outcome of evaluating all applicable policies
 * for a request.
 *
 * An EvaluationResult aggregates policy decisions and reflects the
 * system-level allow or reject outcome. In the case of rejection,
 * the result identifies the policy responsible and exposes relevant
 * decision details.
 *
 * Instances are immutable and safe to share across threads.
 */
public final class EvaluationResult {
    public enum Status {
        ALLOW,
        REJECT
    }

    private final Status status;
    private final Optional<String> rejectingPolicyId;
    private final Optional<PolicyDecision> policyDecision;

    private EvaluationResult(
            Status status,
            Optional<String> rejectingPolicyId,
            Optional<PolicyDecision> policyDecision
    ) {
        this.status = status;
        this.rejectingPolicyId = rejectingPolicyId;
        this.policyDecision = policyDecision;
    }

    public static EvaluationResult allow() {
        return new EvaluationResult(Status.ALLOW, Optional.empty(), Optional.empty());
    }

    public static EvaluationResult reject(
            String policyId,
            PolicyDecision decision
    ) {
        return new EvaluationResult(
                Status.REJECT,
                Optional.of(policyId),
                Optional.of(decision)
        );
    }

    public Status status() {
        return status;
    }

    public Optional<String> rejectingPolicyId() {
        return rejectingPolicyId;
    }

    public Optional<PolicyDecision> policyDecision() {
        return policyDecision;
    }
}
