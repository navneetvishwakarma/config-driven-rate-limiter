package com.vkteenvan.ratelimiter.policy;

import com.vkteenvan.ratelimiter.decision.PolicyDecision;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fixed-window rate limiting policy.
 *
 * <p>
 * This policy enforces a maximum number of allowed requests within a fixed,
 * time-aligned window. Requests are counted against the current window and
 * rejected once the configured limit is reached.
 * </p>
 *
 * <h2>Window Semantics</h2>
 * <ul>
 *   <li>Time is partitioned into contiguous, fixed-size windows aligned to
 *       epoch time boundaries.</li>
 *   <li>All requests whose evaluation time falls within the same window
 *       share a common counter.</li>
 *   <li>When the active window expires, the counter is reset and evaluation
 *       continues against the new window.</li>
 * </ul>
 *
 * <h2>Concurrency Guarantees</h2>
 * <ul>
 *   <li>Evaluation is thread-safe and linearizable.</li>
 *   <li>No more than {@code limit} requests will be allowed within any single
 *       window, even under concurrent access.</li>
 *   <li>Window reset and counter increment occur as a single atomic state
 *       transition.</li>
 *   <li>The implementation does not rely on global or coarse-grained locks.</li>
 * </ul>
 *
 * <h2>Failure and Edge-Case Behavior</h2>
 * <ul>
 *   <li>Requests arriving at a window boundary are evaluated deterministically
 *       based on their observed evaluation time.</li>
 *   <li>Rejections may include a best-effort retry-after duration indicating
 *       when the next window begins.</li>
 *   <li>All state is maintained in memory and discarded on process restart.</li>
 * </ul>
 *
 * <h2>Limitations</h2>
 * <ul>
 *   <li>This policy is single-node and does not coordinate state across
 *       processes.</li>
 *   <li>It does not provide fairness guarantees within a window.</li>
 *   <li>Throughput optimization and time precision are secondary to
 *       correctness and determinism.</li>
 * </ul>
 *
 * <p>
 * This policy is intended as a simple, deterministic baseline for rate
 * limiting and as a building block for higher-level orchestration.
 * </p>
 */
public final class FixedWindowPolicy implements IPolicy {

    private final String policyId;
    private final int limit;
    private final long windowSeconds;
    private final Clock clock;

    private final AtomicReference<WindowState> state;

    public FixedWindowPolicy(
            String policyId,
            int limit,
            long windowSeconds,
            Clock clock
    ) {
        this.policyId = policyId;
        this.limit = limit;
        this.windowSeconds = windowSeconds;
        this.clock = clock;

        long now = currentWindowStart();
        this.state = new AtomicReference<>(new WindowState(now, 0));
    }

    @Override
    public PolicyDecision evaluate() {
        while (true) {
            WindowState current = state.get();
            long nowWindowStart = currentWindowStart();

            // Case 1: window expired → reset
            if (current.windowStart != nowWindowStart) {
                WindowState reset = new WindowState(nowWindowStart, 1);
                if (state.compareAndSet(current, reset)) {
                    return PolicyDecision.allow(
                            Optional.of((long) (limit - 1))
                    );
                }
                continue;
            }

            // Case 2: same window → increment
            if (current.counter < limit) {
                WindowState updated =
                        new WindowState(current.windowStart, current.counter + 1);

                if (state.compareAndSet(current, updated)) {
                    return PolicyDecision.allow(
                            Optional.of((long) (limit - updated.counter))
                    );
                }
                continue;
            }

            // Case 3: limit exceeded
            return PolicyDecision.reject(
                    Optional.of(retryAfterDuration(current.windowStart))
            );
        }
    }

    @Override
    public String policyId() {
        return policyId;
    }

    // ---------- helpers ----------

    private long currentWindowStart() {
        long epochSeconds = Instant.now(clock).getEpochSecond();
        return epochSeconds - (epochSeconds % windowSeconds);
    }

    private java.time.Duration retryAfterDuration(long windowStart) {
        long now = Instant.now(clock).getEpochSecond();
        long retryAt = windowStart + windowSeconds;
        long seconds = Math.max(0, retryAt - now);
        return java.time.Duration.ofSeconds(seconds);
    }

    private static final class WindowState {
        final long windowStart;
        final int counter;

        WindowState(long windowStart, int counter) {
            this.windowStart = windowStart;
            this.counter = counter;
        }
    }
}

