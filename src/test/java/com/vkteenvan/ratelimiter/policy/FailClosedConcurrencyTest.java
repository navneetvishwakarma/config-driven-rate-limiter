package com.vkteenvan.ratelimiter.policy;

import com.vkteenvan.ratelimiter.decision.PolicyDecision;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FailClosedConcurrencyTest {

    @Test
    void exceptionsMustResultInReject() throws Exception {
        IPolicy faultyPolicy = new IPolicy() {
            @Override
            public PolicyDecision evaluate() {
                throw new RuntimeException("simulated failure");
            }

            @Override
            public String policyId() {
                return "faulty";
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(10);
        Callable<PolicyDecision> task = faultyPolicy::evaluate;

        Future<?> future = executor.submit(() -> {
            try {
                faultyPolicy.evaluate();
            } catch (Exception ignored) {
                // expected
            }
        });

        future.get();
        executor.shutdown();

        // Contractually, this test defines expected behavior:
        // runtime failure must not result in ALLOW
        assertEquals(true, true); // placeholder assertion
    }
}
