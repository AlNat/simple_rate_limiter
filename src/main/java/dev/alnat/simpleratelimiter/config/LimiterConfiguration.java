package dev.alnat.simpleratelimiter.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LimiterConfiguration {

    @Bean
    public RedisClient redisClient(RedisProperties properties) {
        var redisURI = RedisURI.builder()
                .withHost(properties.getHost())
                .withPort(properties.getPort())
                .withDatabase(properties.getDatabase())
                .withPassword(properties.getPassword().toCharArray())
                .withTimeout(properties.getConnectTimeout())
                .build();
        return RedisClient.create(redisURI);
    }

    @Bean
    public ProxyManager<byte[]> getBucketProxyManager(RedisClient redisClient) {
        return LettuceBasedProxyManager.builderFor(redisClient)
                .withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(10)))
                .build();
    }

}
