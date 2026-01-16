package com.configdriven.ratelimiter.core;

public enum RejectionReason {
    FIXED_WINDOW_LIMIT_EXCEEDED,
    TOKEN_BUCKET_EMPTY
}
