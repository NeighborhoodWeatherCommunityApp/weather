package org.pknu.weather.alarm.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.alarm.enums.AlarmType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmCooldownService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String COOLDOWN_KEY_PREFIX = "alarm:cooldown:";
    private static final Duration DEFAULT_COOLDOWN_DURATION = Duration.ofHours(4);

    public boolean isInCooldown(AlarmType type, String identifier) {
        String key = generateKey(type, identifier);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setCooldown(AlarmType type, String identifier) {
        String key = generateKey(type, identifier);
        redisTemplate.opsForValue().set(key, "1", DEFAULT_COOLDOWN_DURATION);
    }

    public void setCooldown(AlarmType type, String identifier, int hour) {
        String key = generateKey(type, identifier);
        redisTemplate.opsForValue().set(key, "1", Duration.ofHours(hour));
    }

    private String generateKey(AlarmType type,String identifier) {
        return COOLDOWN_KEY_PREFIX + type.name().toLowerCase() + ":" + identifier;
    }
}
