package com.ritvik.zodiacverseBackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * AI Chat specific rate limiter
     * Allows 10 messages per minute per user
     */
    public boolean isAIChatAllowed(String email) {
        return isAllowed("ai:chat:" + email, 10, Duration.ofMinutes(1));
    }

    /**
     * Generic rate limiter — reuse for any future endpoint
     * @param key      unique identifier (email, IP etc.)
     * @param maxReqs  max requests allowed in the window
     * @param window   time window duration
     */
    private boolean isAllowed(String key, int maxReqs, Duration window) {
        try {
            Long count = redisTemplate.opsForValue().increment(key);

            if (count == null) return true; // Redis error — allow request

            // First request in window — set expiry
            if (count == 1) {
                redisTemplate.expire(key, window);
            }

            if (count > maxReqs) {
                log.warn(" Rate limit hit — key={} count={}/{}", key, count, maxReqs);
                return false;
            }

            log.debug(" Rate limit ok — key={} count={}/{}", key, count, maxReqs);
            return true;

        } catch (Exception e) {
            // If Redis is down — fail open (allow request)
            log.error(" Redis rate limiter error: {} — allowing request", e.getMessage());
            return true;
        }
    }
}