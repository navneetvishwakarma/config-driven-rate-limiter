package com.configdriven.ratelimiter.service;

import com.configdriven.ratelimiter.config.ClientConfig;
import com.configdriven.ratelimiter.config.PolicyConfig;
import com.configdriven.ratelimiter.core.PolicyType;
import com.configdriven.ratelimiter.core.RateLimitDecision;
import com.configdriven.ratelimiter.core.RejectionReason;
import com.configdriven.ratelimiter.storage.InMemoryStateStore;
import com.configdriven.ratelimiter.storage.StateStore;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateLimitServiceTest {
    @Test
    void fixedWindowRejectsSecondRequestInWindow() {
        StateStore store = new InMemoryStateStore();
        RateLimitService service = new RateLimitService(store);
        ClientConfig config = new ClientConfig(
                "client-A",
                List.of(new PolicyConfig("fixed", PolicyType.FIXED_WINDOW, 1, 60, null, null))
        );
        Instant now = Instant.parse("2025-01-01T00:00:00Z");

        RateLimitDecision first = service.evaluate(config, now);
        RateLimitDecision second = service.evaluate(config, now);

        assertTrue(first.allowed());
        assertFalse(second.allowed());
        assertEquals(RejectionReason.FIXED_WINDOW_LIMIT_EXCEEDED, second.reason());
    }
}
