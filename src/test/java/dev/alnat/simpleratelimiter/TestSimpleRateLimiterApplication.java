package dev.alnat.simpleratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestSimpleRateLimiterApplication {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:15.3-alpine3.18");
    }

    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>("redis:7.0.12-alpine3.18").withExposedPorts(6379);
    }

    public static void main(String[] args) {
        SpringApplication.from(TestSimpleRateLimiterApplication::main).with(TestSimpleRateLimiterApplication.class).run(args);
    }

}
