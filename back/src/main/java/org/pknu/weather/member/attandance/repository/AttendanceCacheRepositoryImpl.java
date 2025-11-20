package org.pknu.weather.member.attandance.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Slf4j
public class AttendanceCacheRepositoryImpl implements AttendanceCacheRepository {
    @Qualifier("stringRedisTemplate")
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean checkIn(String key, Long userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setBit(key, userId, true));
    }

    @Override
    public boolean checkIn(DefaultRedisScript<Long> script, String key, Long userId) {
        Long result = Optional.ofNullable(stringRedisTemplate.execute(script, List.of(key), String.valueOf(userId)))
                .orElseThrow(() -> new GeneralException(ErrorStatus._REDIS_INTERNAL_SERVER_ERROR));

        return result.equals(1L);
    }
}
