package org.pknu.weather.weather.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.pknu.weather.weather.utils.WeatherRedisKeyUtils.buildKey;
import static org.pknu.weather.weather.utils.WeatherRedisKeyUtils.generateHourlyWeatherKeys;

@RequiredArgsConstructor
@Slf4j
@Repository
public class WeatherRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Duration DEFAULT_DURATION = Duration.ofHours(24);
    private final Integer DEFAULT_HOURS = 24;

    public List<WeatherRedisDTO.WeatherData> getWeathers(Long locationId, LocalDateTime localDateTime) {
        List<Object> result = Optional.ofNullable(
                opsForValue().multiGet(generateHourlyWeatherKeys(locationId, localDateTime, DEFAULT_HOURS))
        ).orElse(Collections.emptyList());

        return result.stream()
                .filter(Objects::nonNull)
                .map(obj -> (WeatherRedisDTO.WeatherData) obj)
                .toList();
    }

    public void saveWeather(Long locationId, WeatherRedisDTO.WeatherData weatherData, Duration duration) {
        opsForValue().set(buildKey(locationId, weatherData.getPresentationTime()), weatherData, duration);
    }

    public void saveWeatherList(Long locationId, List<WeatherRedisDTO.WeatherData> weatherDataList) {
        for (WeatherRedisDTO.WeatherData weatherData : weatherDataList) {
            opsForValue().set(buildKey(locationId, weatherData.getPresentationTime()), weatherData, DEFAULT_DURATION);
        }
    }

    public void updateWeather(Long locationId,WeatherRedisDTO.WeatherData weatherData) {
        opsForValue().set(buildKey(locationId, weatherData.getPresentationTime()), weatherData, DEFAULT_DURATION);
    }

    public void deleteValues(Long locationId, LocalDateTime presentationTime) {
        redisTemplate.delete(buildKey(locationId, presentationTime));
    }

    /**
     * TTL을 지금부터 timeout만큼 지난 후에 만료되도록 재설정합니다.
     * <p>
     * ex.
     * TTL 5초 설정
     * redisTemplate.expire("user:1", 5, TimeUnit.SECONDS);
     * TTL 다시 10초로 설정 (늘어남)
     * redisTemplate.expire("user:1", 10, TimeUnit.SECONDS);
     *
     * @param locationId
     * @param presentationTime 날씨 예보 시각 (1시간 단위)
     * @param timeout
     */
    public void expireWeather(Long locationId, LocalDateTime presentationTime, int timeout) {
        redisTemplate.expire(buildKey(locationId, presentationTime), timeout, TimeUnit.HOURS);
    }

    private ValueOperations<String, Object> opsForValue() {
        return redisTemplate.opsForValue();
    }
}
