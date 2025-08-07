package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/health-check")
public class HealthCheckController {
    private final RedisTemplate<String, String> redisTemplate;

    public Map<String, Integer> healthCheck() {
        Map<String, Integer> map = new HashMap<>();
        map.put("code", 200);
        return map;
    }

    @GetMapping("/redis")
    public ApiResponse<Map<String, Object>> checkRedisHealth() {
        Map<String, Object> response = new HashMap<>();

        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection connection = (factory != null) ? factory.getConnection() : null;

        if (connection != null) {
            try {
                String ping = connection.ping();
                response.put("ping", ping);
                response.put("status", "PONG".equals(ping) ? "UP" : "DOWN");
            } catch (Exception e) {
                log.warn("Redis ping failed: {}", e.getMessage());
                response.put("ping", "Failed");
                response.put("status", "DOWN");
            } finally {
                connection.close();
            }
        } else {
            response.put("ping", "No Connection");
            response.put("status", "DOWN");
        }

        if (factory instanceof LettuceConnectionFactory lettuceFactory) {
            response.put("host", lettuceFactory.getHostName());
            response.put("port", lettuceFactory.getPort());
            response.put("ssl", lettuceFactory.isUseSsl());
            Duration timeout = lettuceFactory.getClientConfiguration().getCommandTimeout();
            response.put("timeout", timeout != null ? timeout.toMillis() : null);
        }

        return ApiResponse.onSuccess(response);
    }
}
