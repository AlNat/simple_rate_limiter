package dev.alnat.simpleratelimiter.service;

import dev.alnat.simpleratelimiter.repository.ClientSettingRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class BucketLimiterService implements LimiterService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private final ClientSettingRepository repository;
    private final ProxyManager<byte[]> bucketProxyManager;

    @Override
    public Optional<Bucket> resolveBucket(String key) {
        // If another thread creates new one in cache -- last will win
        if (!cache.containsKey(key)) {
            var bucketOpt = initNewBucket(key);
            if (bucketOpt.isEmpty()) {
                return Optional.empty();
            }
        }

        return Optional.of(cache.get(key));
    }

    private Optional<Bucket> initNewBucket(String apiKey) {
        var clientSettingsOpt = repository.findClientSettingByApiKey(apiKey);
        if (clientSettingsOpt.isEmpty()) {
            return Optional.empty();
        }

        // Simple disturbed limit
        var configuration = BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(clientSettingsOpt.get().getRpm(), Duration.ofMinutes(1L)))
                .build();
        var bucket = bucketProxyManager.builder().build(apiKey.getBytes(), configuration);

        cache.put(apiKey, bucket);
        return Optional.of(bucket);
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
