package org.pknu.weather.weather.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.pknu.weather.weather.utils.WeatherRedisKeyUtils.buildKey;
import static org.pknu.weather.weather.utils.WeatherRedisKeyUtils.generateHourlyWeatherKeys;

@RequiredArgsConstructor
@Slf4j
@Repository
public class WeatherRedisRepository {
    @Qualifier("jsonRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final static Duration DEFAULT_DURATION = Duration.ofHours(24);
    private final static Integer DEFAULT_HOURS = 24;

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

    public void updateWeather(Long locationId, WeatherRedisDTO.WeatherData weatherData) {
        opsForValue().set(buildKey(locationId, weatherData.getPresentationTime()), weatherData, DEFAULT_DURATION);
    }

    public void deleteValues(Long locationId, LocalDateTime presentationTime) {
        redisTemplate.delete(buildKey(locationId, presentationTime));
    }

    public List<WeatherRedisDTO.WeatherData> getWeatherList(Long locationId) {
        List<Object> objectList = opsForList().range(buildKey(locationId), 0, -1);

        return objectList.stream()
                .filter(Objects::nonNull)
                .map(obj -> (WeatherRedisDTO.WeatherData) obj)
                .toList();
    }

    public void rightPushAll(Long locationId, List<WeatherRedisDTO.WeatherData> weatherDataList) {
        for (WeatherRedisDTO.WeatherData weatherData : weatherDataList) {
            opsForList().rightPush(buildKey(locationId), weatherData);
        }

        int jitter = ThreadLocalRandom.current().nextInt(0, 11);
        redisTemplate.expire(buildKey(locationId), DEFAULT_DURATION.plusMinutes(jitter));
    }

    public void updateWeatherList(Long locationId, List<WeatherRedisDTO.WeatherData> weatherDataList) {
        redisTemplate.delete(buildKey(locationId));
        rightPushAll(locationId, weatherDataList);
    }

    public void deleteWeatherList(Long locationId) {
        redisTemplate.delete(buildKey(locationId));
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

    private ListOperations<String, Object> opsForList() {
        return redisTemplate.opsForList();
    }
}
