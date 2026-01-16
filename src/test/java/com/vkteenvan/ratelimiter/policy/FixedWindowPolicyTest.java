package com.vkteenvan.ratelimiter.policy;

import com.vkteenvan.ratelimiter.decision.PolicyDecision;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedWindowPolicyTest {

    @Test
    void resetsWindowAndAllowsUpToLimitAgain() {
        MutableClock clock = new MutableClock(
                Instant.parse("2025-01-01T00:00:00Z"),
                ZoneOffset.UTC
        );
        FixedWindowPolicy policy = new FixedWindowPolicy("fixed", 2, 10, clock);

        assertEquals(PolicyDecision.Status.ALLOW, policy.evaluate().status());
        assertEquals(PolicyDecision.Status.ALLOW, policy.evaluate().status());
        assertEquals(PolicyDecision.Status.REJECT, policy.evaluate().status());

        clock.advanceSeconds(10);

        assertEquals(PolicyDecision.Status.ALLOW, policy.evaluate().status());
        assertEquals(PolicyDecision.Status.ALLOW, policy.evaluate().status());
        assertEquals(PolicyDecision.Status.REJECT, policy.evaluate().status());
    }

    private static final class MutableClock extends Clock {
        private final ZoneId zoneId;
        private final AtomicLong currentMillis;

        private MutableClock(Instant initialInstant, ZoneId zoneId) {
            this.zoneId = zoneId;
            this.currentMillis = new AtomicLong(initialInstant.toEpochMilli());
        }

        void advanceSeconds(long seconds) {
            currentMillis.addAndGet(seconds * 1000);
        }

        @Override
        public ZoneId getZone() {
            return zoneId;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(instant(), zone);
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(currentMillis.get());
        }
    }
}
