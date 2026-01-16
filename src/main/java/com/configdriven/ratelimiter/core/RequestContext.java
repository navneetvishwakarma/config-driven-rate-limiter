package com.configdriven.ratelimiter.core;

import java.time.Instant;

public record RequestContext(String clientId, Instant timestamp) {}
