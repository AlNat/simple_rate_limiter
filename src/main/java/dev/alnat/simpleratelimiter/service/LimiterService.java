package dev.alnat.simpleratelimiter.service;

import io.github.bucket4j.Bucket;

import java.util.Optional;

public interface LimiterService {

    /**
     * Find bucket by client
     */
    Optional<Bucket> resolveBucket(String apiKey);

    /**
     * On change to apply new limits
     */
    void clearCache();

}
