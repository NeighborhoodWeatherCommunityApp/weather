package org.pknu.weather.weather.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.enums.RainType;
import org.pknu.weather.weather.enums.SkyType;
import org.pknu.weather.weather.feignclient.utils.WeatherFeignClientUtils;
import org.pknu.weather.weather.repository.WeatherRedisRepository;
import org.pknu.weather.weather.utils.WeatherRedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Import(EmbeddedRedisConfig.class)
class WeatherCacheServiceTest {
    @Autowired
    WeatherRedisRepository weatherRedisRepository;

    @MockBean
    WeatherFeignClientUtils weatherFeignClientUtils;

    @MockBean
    LocationRepository locationRepository;

    @SpyBean
    WeatherCacheService weatherCacheService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Location location;
    private final LocalDateTime now = LocalDateTime.of(2025, 1, 1, 2, 0);
    private List<Weather> weatherList;

    @BeforeEach
    void clearRedis() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll(); // 모든 데이터 삭제

        weatherList = new ArrayList<>();
        location = TestDataCreator.getBusanLocation();

        for (int i = 0; i < 24; i++) {
            LocalDateTime presentationTime = now.plusHours(i);
            weatherList.add(Weather.builder()
                    .location(location)
                    .presentationTime(presentationTime)
                    .basetime(now)      // 02시
                    .windSpeed(3.5)
                    .humidity(60)
                    .rainProb(40)
                    .rain(0.0f)
                    .rainType(RainType.NONE)
                    .temperature(28)
                    .sensibleTemperature(30.0)
                    .snowCover(0.0f)
                    .skyType(SkyType.CLEAR)
                    .build());
        }
    }

    @Test
    void updateWeatherDataScheduled_성공테스트() {
        // given
        when(locationRepository.safeFindById(location.getId())).thenReturn(location);
        when(weatherFeignClientUtils.getVillageShortTermForecast(location)).thenReturn(weatherList);

        // when
        weatherCacheService.updateCachedWeathersForLocation(location.getId());

        // then
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < weatherList.size(); i++) {
            String key = WeatherRedisKeyUtils.buildKey(weatherList.get(0).getLocation().getId(), now.plusHours(i));
            keyList.add(key);
        }
        List<Object> objects = redisTemplate.opsForValue().multiGet(keyList);
        Assertions.assertThat(objects.isEmpty()).isEqualTo(false);
        Assertions.assertThat(objects.size()).isEqualTo(24);

        verify(weatherFeignClientUtils, times(1)).getVillageShortTermForecast(location);
    }
}