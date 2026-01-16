package com.vkteenvan.ratelimiter.service;

import com.vkteenvan.ratelimiter.config.ClientConfig;
import com.vkteenvan.ratelimiter.config.PolicyConfig;
import com.vkteenvan.ratelimiter.core.PolicyType;
import com.vkteenvan.ratelimiter.core.RateLimitDecision;
import com.vkteenvan.ratelimiter.core.RejectionReason;
import com.vkteenvan.ratelimiter.storage.InMemoryStateStore;
import com.vkteenvan.ratelimiter.storage.StateStore;
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
