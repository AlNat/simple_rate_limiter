package dev.alnat.simpleratelimiter.api;

import dev.alnat.simpleratelimiter.service.LimiterService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static dev.alnat.simpleratelimiter.api.Const.*;

@Component
@RequiredArgsConstructor
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final LimiterService limiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        final String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || apiKey.isEmpty()) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing Header: " + API_KEY_HEADER);
            return false;
        }

        var bucketOpt =  limiterService.resolveBucket(apiKey);
        if (bucketOpt.isEmpty()) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid auth key: " + apiKey);
            return false;
        }

        Bucket tokenBucket = bucketOpt.get();
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.addHeader(REMAINING_HEADER, String.valueOf(probe.getRemainingTokens()));
            return true;
        }

        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        response.addHeader(RETRY_SECONDS_HEADER, String.valueOf(waitForRefill));
        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API Request Quota");
        return false;
    }

}
