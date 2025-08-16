package org.pknu.weather.weather.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.config.EmbeddedRedisConnectionFactory;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.domain.common.SkyType;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.pknu.weather.weather.utils.WeatherRedisKeyUtils.buildKey;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({EmbeddedRedisConfig.class, EmbeddedRedisConnectionFactory.class})
class WeatherRedisRepositoryTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WeatherRedisRepository weatherRedisRepository;

    private final Long locationId = 1L;
    private final LocalDateTime now = LocalDateTime.of(2025, 1, 1, 2, 0);
    private List<WeatherRedisDTO.WeatherData> weatherList;

    @BeforeEach
    void clearRedis() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll(); // 모든 데이터 삭제

        weatherList = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            LocalDateTime presentationTime = now.plusHours(i);

            weatherList.add(WeatherRedisDTO.WeatherData.builder()
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

        for (int i = 24; i < 27; i++) {
            LocalDateTime presentationTime = now.plusHours(i);

            weatherList.add(WeatherRedisDTO.WeatherData.builder()
                    .presentationTime(presentationTime)
                    .basetime(now.plusHours(3))        // 05시
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
    void getWeather_반환성공테스트() {
        // given
        for (int i = 0; i < 24; i++) {
            weatherRedisRepository.saveWeather(locationId, weatherList.get(i), Duration.ofHours(24));
        }

        // when
        List<WeatherRedisDTO.WeatherData> weathers = weatherRedisRepository.getWeathers(locationId, now);

        // then
        assertThat(weathers.size()).isEqualTo(24);
    }

    @Test
    void getWeather_null반환테스트() {
        // given

        // when
        List<WeatherRedisDTO.WeatherData> weathers = weatherRedisRepository.getWeathers(locationId, now);

        // then
        assertThat(weathers.size()).isEqualTo(0);
    }

    @Test
    void saveWeather_정상저장() {
        // given
        String key = buildKey(locationId, now);
        weatherRedisRepository.saveWeather(locationId, weatherList.get(0), Duration.ofHours(24));

        //when
        Object result = redisTemplate.opsForValue().get(key);

        //then
        assertThat(result).isInstanceOf(WeatherRedisDTO.WeatherData.class);
        assertThat(((WeatherRedisDTO.WeatherData) result).getTemperature()).isEqualTo(28);
    }

    @Test
    void updateWeather_기존값덮어쓰기() {
        // given
        String key = buildKey(locationId, now);
        Integer pastTemperature = 35;
        WeatherRedisDTO.WeatherData updatedData = WeatherRedisDTO.WeatherData.builder()
                .presentationTime(now)
                .basetime(now)        // 05시
                .windSpeed(3.5)
                .humidity(60)
                .rainProb(40)
                .rain(0.0f)
                .rainType(RainType.NONE)
                .temperature(pastTemperature)
                .sensibleTemperature(30.0)
                .snowCover(0.0f)
                .skyType(SkyType.CLEAR)
                .build();

        weatherRedisRepository.updateWeather(locationId, updatedData);

        // when
        WeatherRedisDTO.WeatherData result = (WeatherRedisDTO.WeatherData)
                redisTemplate.opsForValue().get(key);

        // then
        assertThat(result.getTemperature()).isEqualTo(35);
    }

    @Test
    void expireWeather_TTL_적용됨() {
        // when
        weatherRedisRepository.saveWeather(locationId, weatherList.get(0), Duration.ofHours(21));
        weatherRedisRepository.expireWeather(locationId, now, 24); // 1시간 TTL

        // then
        Long ttl = redisTemplate.getExpire(buildKey(locationId, now), TimeUnit.HOURS);
        assertThat(ttl).isLessThanOrEqualTo(24);
    }

    @Test
    void deleteValues_정상삭제() {
        // when
        weatherRedisRepository.saveWeather(locationId, weatherList.get(0), Duration.ofHours(24));
        weatherRedisRepository.deleteValues(locationId, now);

        // then
        Object result = redisTemplate.opsForValue().get(buildKey(locationId, now));
        assertThat(result).isNull();
    }
}