package org.pknu.weather.weather;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.weather.utils.SensibleTemperatureUtils;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
class WeatherTest {
    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    void weather_엔티티는_영속화_되기전_체감_온도를_업데이트_한다() {
        // given
        Weather weather = Weather.builder()
                .temperature(14)
                .humidity(50)
                .windSpeed(1.5)
                .presentationTime(LocalDateTime.now())
                .build();

        // when
        weatherRepository.save(weather);

        // then
        Assertions.assertThat(weather.getSensibleTemperature()).isEqualTo(
                SensibleTemperatureUtils.getSensibleTemperature(weather.getTemperature(), weather.getHumidity(),
                        weather.getWindSpeed()));
    }
}