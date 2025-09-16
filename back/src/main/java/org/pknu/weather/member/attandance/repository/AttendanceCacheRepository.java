package org.pknu.weather.member.attandance.repository;

import org.springframework.data.redis.core.script.DefaultRedisScript;

public interface AttendanceCacheRepository {
    boolean checkIn(String key, Long userId);

    boolean checkIn(DefaultRedisScript<Boolean> script, String key, Long userId);
}
