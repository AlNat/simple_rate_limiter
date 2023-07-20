package dev.alnat.simpleratelimiter.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Const {

    public static final String API_KEY_HEADER = "X-API-KEY";
    public static final String REMAINING_HEADER = "X-RATE-LIMIT-REMAINING";
    public static final String RETRY_SECONDS_HEADER = "X-RATE-LIMIT-RETRY-AFTER-SECONDS";

}
