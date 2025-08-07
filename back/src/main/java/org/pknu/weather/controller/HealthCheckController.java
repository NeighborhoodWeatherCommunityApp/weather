package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
    public ResponseEntity<Map<String, Object>> checkRedisHealth() {
//        try {
            RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
            RedisConnection connection = factory != null ? factory.getConnection() : null;

            Map<String, Object> response = new HashMap<>();

            if (connection != null) {
                Properties config = connection.getConfig("requirepass");
                response.put("password", config == null ? "not set" : "set");
                response.put("ping", connection.ping());
            } else {
                response.put("password", "unknown");
                response.put("ping", "No Response");
            }

            response.put("port", 6379); // 기본 포트
            if (factory instanceof LettuceConnectionFactory lettuceFactory) {
                response.put("host", lettuceFactory.getHostName());
                response.put("ssl", lettuceFactory.isUseSsl());
                Duration timeout = lettuceFactory.getClientConfiguration().getCommandTimeout();
                response.put("timeout", timeout.toMillis());
            }

            String ping = (String) response.get("ping");
            response.put("status", "PONG".equals(ping) ? "UP" : "DOWN");

            return ResponseEntity.ok(response);

//        } catch (Exception e) {
//            Map<String, Object> error = new HashMap<>();
//            log.info(e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
    }
}
