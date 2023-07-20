package dev.alnat.simpleratelimiter.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Const {

    public static final String API_KEY_HEADER = "X_API_KEY";
    public static final String REMAINING_HEADER = "X_RATE_LIMIT_REMAINING";
    public static final String RETRY_SECONDS_HEADER = "X_RATE_LIMIT_RETRY_AFTER_SECONDS";

}
