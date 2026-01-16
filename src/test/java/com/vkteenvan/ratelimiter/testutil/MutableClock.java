package com.vkteenvan.ratelimiter.testutil;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A mutable Clock implementation for deterministic time control in tests.
 *
 * This clock is thread-safe and allows manual advancement of time.
 * Intended strictly for testing time-dependent logic.
 */
public final class MutableClock extends Clock {

    private final AtomicReference<Instant> current;
    private final ZoneId zone;

    public MutableClock(Instant initialInstant) {
        this(initialInstant, ZoneId.of("UTC"));
    }

    public MutableClock(Instant initialInstant, ZoneId zone) {
        this.current = new AtomicReference<>(initialInstant);
        this.zone = zone;
    }

    /**
     * Advances the clock by the given amount.
     */
    public void advanceBy(long amount, TemporalUnit unit) {
        current.updateAndGet(i -> i.plus(amount, unit));
    }

    @Override
    public Instant instant() {
        return current.get();
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new MutableClock(current.get(), zone);
    }
}
