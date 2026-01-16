package com.vkteenvan.ratelimiter.core;

public enum RejectionReason {
    FIXED_WINDOW_LIMIT_EXCEEDED,
    TOKEN_BUCKET_EMPTY
}
