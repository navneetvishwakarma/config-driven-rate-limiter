package com.vkteenvan.ratelimiter.storage;

public interface StateStore {
    FixedWindowState getFixedWindowState(String clientId, String policyId);

    TokenBucketState getTokenBucketState(String clientId, String policyId);
}
