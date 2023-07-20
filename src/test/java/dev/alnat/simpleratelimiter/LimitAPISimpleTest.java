package dev.alnat.simpleratelimiter;

import dev.alnat.simpleratelimiter.model.ClientSetting;
import dev.alnat.simpleratelimiter.repository.ClientSettingRepository;
import dev.alnat.simpleratelimiter.service.LimiterService;
import lombok.SneakyThrows;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static dev.alnat.simpleratelimiter.api.Const.REMAINING_HEADER;
import static dev.alnat.simpleratelimiter.api.Const.RETRY_SECONDS_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class LimitAPISimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientSettingRepository repository;

    @Autowired
    private LimiterService limiterService;

    private static final String TEST_KEY = "SOME_TEST_KEY";

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer<>("postgres:15.3-alpine3.18");

    @Container
    @ServiceConnection
    static GenericContainer redis = new GenericContainer<>("redis:7.0.12-alpine3.18").withExposedPorts(6379);


    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(redis.getMappedPort(6379)));
    }


    @AfterEach
    void cleanUp() {
        repository.deleteAll();
        limiterService.clearCache();
    }


    @Test
    @SneakyThrows
    void testGetWithoutHeaderKey() {
        this.mockMvc.perform(
                        get("/api/v1/client/data/" + "1")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();
    }

    @Test
    @SneakyThrows
    void testGetWhenNoKeyInDB() {
        this.mockMvc.perform(
                        get("/api/v1/client/data/" + "1").header("X-API-KEY", TEST_KEY)
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    @SneakyThrows
    void testGetWhenKeyWithTenLimitInDB() {
        repository.save(ClientSetting.of(TEST_KEY + "10", 10));

        this.mockMvc.perform(
                        get("/api/v1/client/data/" + "1").header("X-API-KEY", TEST_KEY + "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(REMAINING_HEADER))
                .andExpect(header().longValue(REMAINING_HEADER, 9))
                .andReturn()
                .getResponse();
    }

    @Test
    @SneakyThrows
    void testGetWhenKeyWithOneLimitInDBWithTwoQueries() {
        repository.save(ClientSetting.of(TEST_KEY, 1));

        mockMvc.perform(
                        get("/api/v1/client/data/" + "1").header("X-API-KEY", TEST_KEY)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(REMAINING_HEADER))
                .andExpect(header().longValue(REMAINING_HEADER, 0))
                .andReturn()
                .getResponse();

        mockMvc.perform(
                        get("/api/v1/client/data/" + "2").header("X-API-KEY", TEST_KEY)
                )
                .andDo(print())
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists(RETRY_SECONDS_HEADER))
                .andExpect(header().string(RETRY_SECONDS_HEADER, new BaseMatcher<String>() {
                            @Override
                            public boolean matches(Object actual) {
                                // match remaing seconds
                                if (actual instanceof String s) {
                                    long l = Long.parseLong(s);
                                    return l > 0 && l < 60;
                                }
                                return false;
                            }

                            @Override
                            public void describeTo(Description description) {
                            }
                        }))
                .andReturn()
                .getResponse();
    }

}
