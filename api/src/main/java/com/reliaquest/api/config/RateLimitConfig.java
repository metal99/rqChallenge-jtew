package com.reliaquest.api.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import java.time.Duration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class RateLimitConfig {

    @Value("${resilience4j.ratelimiter.instances.rqRateLimiter.limit-for-period}")
    private int limitForPeriod;

    @Value("${resilience4j.ratelimiter.instances.rqRateLimiter.limit-refresh-period}")
    private int limitRefreshPeriod;

    @Value("${resilience4j.ratelimiter.instances.rqRateLimiter.timeout-duration}")
    private int timeoutDuration;

    @Bean
    public RateLimiter rateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(limitForPeriod)
                .limitRefreshPeriod(Duration.ofSeconds(limitRefreshPeriod))
                .timeoutDuration(getTimeoutDuration(timeoutDuration))
                .build();
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        registry.getEventPublisher()
                .onEntryAdded(entry -> log.info("Rate limiter entry added: {}", entry))
                .onEntryRemoved(event -> log.info(
                        "Rate limiter removed: {}", event.getRemovedEntry().getName()))
                .onEvent(event -> log.info("Rate limiter event: {}", event));
        return registry.rateLimiter("rqRateLimiter");
    }

    private Duration getTimeoutDuration(int timeoutDuration) {
        return Duration.ofSeconds(timeoutDuration);
    }
}
