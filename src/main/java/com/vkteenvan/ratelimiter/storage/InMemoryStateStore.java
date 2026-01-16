package com.vkteenvan.ratelimiter.storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryStateStore implements StateStore {
    private final ConcurrentMap<String, FixedWindowState> fixedWindows = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, TokenBucketState> tokenBuckets = new ConcurrentHashMap<>();

    @Override
    public FixedWindowState getFixedWindowState(String clientId, String policyId) {
        String key = key(clientId, policyId);
        return fixedWindows.computeIfAbsent(key, ignored -> new FixedWindowState());
    }

    @Override
    public TokenBucketState getTokenBucketState(String clientId, String policyId) {
        String key = key(clientId, policyId);
        return tokenBuckets.computeIfAbsent(key, ignored -> new TokenBucketState());
    }

    private String key(String clientId, String policyId) {
        return clientId + ":" + policyId;
    }
}
