package org.pknu.weather.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.config.EmbeddedRedisConnectionFactory;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.domain.common.SkyType;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import({EmbeddedRedisConfig.class, EmbeddedRedisConnectionFactory.class})
public class RedisSerializationTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void clearRedis() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll(); // 모든 데이터 삭제
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
}
