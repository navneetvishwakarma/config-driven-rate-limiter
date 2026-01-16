package com.vkteenvan.ratelimiter.concurrency;

import com.vkteenvan.ratelimiter.config.PolicyConfig;
import com.vkteenvan.ratelimiter.core.PolicyType;
import com.vkteenvan.ratelimiter.decision.PolicyDecision;
import com.vkteenvan.ratelimiter.policy.IPolicy;
import com.vkteenvan.ratelimiter.service.PolicyFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PolicyConcurrencyTest {
    @Test
    void fixedWindowPolicyConcurrentRequestsRespectLimit() throws Exception {
        PolicyConfig config = new PolicyConfig(
                "fixed-window",
                PolicyType.FIXED_WINDOW,
                Map.of("limit", 5, "windowSeconds", 60)
        );
        IPolicy policy = PolicyFactory.create(config, "fixed-window");

        int threads = 20;
        List<PolicyDecision> decisions = evaluateConcurrently(policy, threads);

        long allowed = decisions.stream()
                .filter(decision -> decision.status() == PolicyDecision.Status.ALLOW)
                .count();

        assertEquals(5, allowed);
        assertEquals(threads, decisions.size());
    }

    @Test
    void tokenBucketPolicyConcurrentRequestsRespectCapacityWithoutRefill() throws Exception {
        PolicyConfig config = new PolicyConfig(
                "token-bucket",
                PolicyType.TOKEN_BUCKET,
                Map.of("capacity", 5, "refillRatePerSecond", 0.0)
        );
        IPolicy policy = PolicyFactory.create(config, "token-bucket");

        int threads = 20;
        List<PolicyDecision> decisions = evaluateConcurrently(policy, threads);

        long allowed = decisions.stream()
                .filter(decision -> decision.status() == PolicyDecision.Status.ALLOW)
                .count();

        assertEquals(5, allowed);
        assertEquals(threads, decisions.size());
    }

    private List<PolicyDecision> evaluateConcurrently(IPolicy policy, int threads) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Callable<PolicyDecision>> tasks = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            tasks.add(() -> {
                start.await();
                return policy.evaluate();
            });
        }

        List<Future<PolicyDecision>> futures = new ArrayList<>();
        for (Callable<PolicyDecision> task : tasks) {
            futures.add(executor.submit(task));
        }
        start.countDown();

        List<PolicyDecision> decisions = new ArrayList<>();
        for (Future<PolicyDecision> future : futures) {
            decisions.add(future.get(5, TimeUnit.SECONDS));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        return decisions;
    }
}
