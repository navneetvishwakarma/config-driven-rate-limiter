package com.vkteenvan.ratelimiter.api;

import com.vkteenvan.ratelimiter.decision.EvaluationResult;

/**
 * Entry point for rate limit evaluation.
 *
 * Implementations are responsible for resolving client configuration,
 * coordinating policy evaluation, and returning a single deterministic
 * decision per request.
 *
 * This interface is synchronous by contract. Implementations must be
 * thread-safe and support concurrent evaluation without relying on
 * global locks.
 *
 * The RateLimiter does not contain algorithm-specific logic. All rate
 * limiting behavior is delegated to Policy implementations.
 */
public interface IRateLimiter {

    /**
     * Evaluates a request for the given client.
     *
     * @param clientId unique identifier of the client
     * @return evaluation result indicating allow or reject
     */
    EvaluationResult evaluate(String clientId);
}
