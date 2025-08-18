package org.pknu.weather.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.pknu.weather.weather.enums.RainType;
import org.pknu.weather.weather.enums.SkyType;
import org.pknu.weather.weather.utils.WeatherRedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import({EmbeddedRedisConfig.class, RedisTestConfig.class})
@Slf4j
public class RedisSerializationTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    Location location;
    List<WeatherRedisDTO.WeatherData> weatherDataList;
    private final LocalDateTime now = LocalDateTime.of(2025, 1, 1, 2, 0);

    @BeforeEach
    void clearRedis() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll(); // 모든 데이터 삭제

        weatherDataList = new ArrayList<>();
        location = TestDataCreator.getBusanLocation(1L);

        for (int i = 0; i < 24; i++) {
            LocalDateTime presentationTime = now.plusHours(i);
            weatherDataList.add(WeatherRedisDTO.WeatherData.builder()
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
    void redis_직렬화_역직렬화_테스트() {
        // given
        String key = "key";
        WeatherRedisDTO.WeatherData data = WeatherRedisDTO.WeatherData.builder()
                .basetime(LocalDateTime.now())
                .rainProb(1)
                .presentationTime(LocalDateTime.now())
                .skyType(SkyType.PARTLYCLOUDY)
                .snowCover(1.0f)
                .temperature(1)
                .windSpeed(1.0d)
                .rain(1.0f)
                .rainType(RainType.RAIN)
                .sensibleTemperature(1.0d)
                .humidity(1)
                .build();

        // when
        redisTemplate.opsForValue().set(key, data);
        Object result = redisTemplate.opsForValue().get(key);

        // then
        assertThat(result).isInstanceOf((WeatherRedisDTO.WeatherData.class));
        WeatherRedisDTO.WeatherData weatherData = (WeatherRedisDTO.WeatherData) result;
        assertThat(weatherData.getRainType()).isEqualTo(RainType.RAIN);
    }

    @Test
    void redis_key_세팅별_조회_속도_테스트() {
        // given
        for (WeatherRedisDTO.WeatherData weather : weatherDataList) {
            redisTemplate.opsForHash().put("weather:location:" + location.getId(), weather.getPresentationTime().toString(), weather);
        }

        for (WeatherRedisDTO.WeatherData weather : weatherDataList) {
            redisTemplate.opsForValue().set(WeatherRedisKeyUtils.buildKey(location.getId(), weather.getPresentationTime()), weather);
        }

        // when
        List<Object> list1 = new ArrayList<>();
        long hashStart = System.nanoTime();
        for (WeatherRedisDTO.WeatherData weather : weatherDataList) {
            list1.add(redisTemplate.opsForHash().get("weather:location:" + location.getId(), weather.getPresentationTime().toString()));
        }
        long hashEnd = System.nanoTime();

        List<Object> list2 = new ArrayList<>();
        long valueStart = System.nanoTime();
        for (WeatherRedisDTO.WeatherData weather : weatherDataList) {
            list2.add(redisTemplate.opsForValue().get(WeatherRedisKeyUtils.buildKey(location.getId(), weather.getPresentationTime())));
        }
        long valueEnd = System.nanoTime();

        List<Object> list3;
        long mgetStart = System.nanoTime();
        list3 = redisTemplate.opsForValue().multiGet(WeatherRedisKeyUtils.generateHourlyWeatherKeys(location.getId(), now, 24));
        long mgetEnd = System.nanoTime();

        // then
        Assertions.assertThat(list1.size()).isEqualTo(24);
        Assertions.assertThat(list2.size()).isEqualTo(24);
        Assertions.assertThat(list3.size()).isEqualTo(24);
        log.info("hash: {}ms", (hashEnd - hashStart) / 1_000_000);
        log.info("hash: {}", hashEnd - hashStart);
        log.info("v: {}ms", (valueEnd - valueStart) / 1_000_000);
        log.info("v: {}", valueEnd - valueStart);
        log.info("mget: {}ms", (mgetEnd - mgetStart) / 1_000_000);
        log.info("mget: {}", mgetEnd - mgetStart);
    }


}

@TestConfiguration
class RedisTestConfig {
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        /**
         * 이 설정은 모든 Redis 값에 대해 JSON 형태로 직렬화를 수행하도록 설정합니다.
         * 키에 대해서는 StringRedisSerializer를 사용하여 문자열로 직렬화합니다.
         */
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(getObjectMapper());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    private ObjectMapper getObjectMapper() {
        // 직렬화/역직렬화 설정
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("org.pknu.weather")  // dto + domain 전체 허용
                        .allowIfSubType("java.time")
                        .allowIfSubType("java.util")
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        // Jackson에 LocalDateTime지원
        objectMapper.registerModule(new JavaTimeModule());
        // LocalDateTime, Date 등을 직/역직렬화할 때 숫자(ex. 20251212001212)가 아닌, 문자열(2025.12.12T00:12:12)로 쓰겠다는 설정 (true=숫자, false=문자열)
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 접근 제한자를 무시합니다.
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        return objectMapper;
    }
}
