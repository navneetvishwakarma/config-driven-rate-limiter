package com.vkteenvan.ratelimiter.policy;

import com.vkteenvan.ratelimiter.decision.PolicyDecision;
import com.vkteenvan.ratelimiter.testutil.MutableClock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FixedWindowBoundaryConcurrencyTest {

    @Test
    void concurrentRequestsAcrossWindowBoundaryBehaveCorrectly() throws Exception {
        int limit = 5;
        int threads = limit * 2;

        MutableClock clock = new MutableClock(
                Instant.parse("2026-01-01T00:00:08Z") // 2s before boundary
        );

        FixedWindowPolicy policy = new FixedWindowPolicy(
                "boundary-test",
                limit,
                10,
                clock
        );

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        AtomicInteger window1Allows = new AtomicInteger();
        AtomicInteger window2Allows = new AtomicInteger();

        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch fireWindow1 = new CountDownLatch(1);
        CountDownLatch fireWindow2 = new CountDownLatch(1);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            final boolean firstWindow = i < limit;
            futures.add(executor.submit(() -> {
                ready.countDown();
                if (firstWindow) {
                    fireWindow1.await();
                } else {
                    fireWindow2.await();
                }

                PolicyDecision decision = policy.evaluate();
                if (decision.status() == PolicyDecision.Status.ALLOW) {
                    if (firstWindow) {
                        window1Allows.incrementAndGet();
                    } else {
                        window2Allows.incrementAndGet();
                    }
                }
                return null;
            }));
        }

        // Ensure all threads are ready
        ready.await();

        // Fire first window requests
        fireWindow1.countDown();

        // Advance clock *across* the window boundary
        clock.advanceBy(3, ChronoUnit.SECONDS); // moves from 8 → 11

        // Fire second window requests
        fireWindow2.countDown();

        for (Future<?> f : futures) {
            f.get();
        }

        executor.shutdown();

        // ---- Assertions ----

        // 1. No window exceeds limit
        assertTrue(window1Allows.get() <= limit,
                "Window 1 allowed more than limit");

        assertTrue(window2Allows.get() <= limit,
                "Window 2 allowed more than limit");

        // 2. No under-allow when demand exists
        assertEquals(limit, window1Allows.get(),
                "Window 1 under-allowed despite sufficient demand");

        assertEquals(limit, window2Allows.get(),
                "Window 2 under-allowed despite sufficient demand");

        // 3. First request in new window must be allowed
        assertTrue(window2Allows.get() > 0,
                "First request in new window was not allowed");
    }
}
