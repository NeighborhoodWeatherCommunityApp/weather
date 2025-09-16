package org.pknu.weather.member.attandance.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
@Slf4j
public class AttendanceCacheRepositoryImpl implements AttendanceCacheRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean checkIn(String key, Long userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setBit(key, userId, true));
    }

    @Override
    public boolean checkIn(DefaultRedisScript<Boolean> script, String key, Long userId) {
        return stringRedisTemplate.execute(script, List.of(key), userId, true);
    }
}
