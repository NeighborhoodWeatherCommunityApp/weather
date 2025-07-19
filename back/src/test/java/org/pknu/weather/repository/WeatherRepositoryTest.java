package org.pknu.weather.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Location;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.dto.WeatherQueryResult;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@Import(DataJpaTestConfig.class)
class WeatherRepositoryTest {
    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    LocationRepository locationRepository;

    Location createLocation() {
        return TestDataCreator.getBusanLocation();
    }


    @Test
    @Transactional
    void 특정_지역의_날씨_갱신_시각이_지나_업데이트_되지_않았다면_False() {
        // given
        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.now()
                .withMinute(0)
                .withSecond(0)
                .withNano(0));

        Weather weather = Weather.builder()
                .basetime(now.minusHours(3))
                .presentationTime(now.plusHours(1))
                .location(locationRepository.save(createLocation()))
                .temperature(14)
                .humidity(50)
                .windSpeed(1.5)
                .build();

        Weather weatherEntity = weatherRepository.save(weather);
        Location location = weatherEntity.getLocation();

        // when
        boolean result = weatherRepository.weatherHasBeenUpdated(location);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    void 특정_지역의_날씨_갱신_시각이_지나_업데이트_되었다면_true() {
        // given
        LocalDateTime baseTime = DateTimeFormatter.getBaseLocalDateTime(LocalDateTime.now());

        Weather weather = Weather.builder()
                .basetime(baseTime)
                .presentationTime(TestDataCreator.getLocalDateTimePlusHours(1))
                .location(locationRepository.save(createLocation()))
                .temperature(14)
                .humidity(50)
                .windSpeed(1.5)
                .build();

        Weather weatherEntity = weatherRepository.save(weather);
        Location location = weatherEntity.getLocation();

        // when
        boolean result = weatherRepository.weatherHasBeenUpdated(location);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @Transactional
    void 특정_지역의_날씨가_등록되지_않았다면_false() {
        // give
        Location location = locationRepository.save(createLocation());

        // when
        boolean result = weatherRepository.weatherHasBeenCreated(location);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    void 특정_지역의_날씨가_등록되어_있다면_true() {
        // give
        LocalDateTime baseTime = DateTimeFormatter.getBaseLocalDateTime(LocalDateTime.now());

        Weather weather = Weather.builder()
                .basetime(baseTime)
                .presentationTime(TestDataCreator.getLocalDateTimePlusHours(1))
                .location(locationRepository.save(createLocation()))
                .temperature(14)
                .humidity(50)
                .windSpeed(1.5)
                .build();

        Weather weatherEntity = weatherRepository.save(weather);
        Location location = weatherEntity.getLocation();

        // when
        boolean result = weatherRepository.weatherHasBeenCreated(location);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @Transactional
    void 간단_강수_정보_테스트_강수_확률이_있을_때() {
        // given
        Location location = locationRepository.save(createLocation());
        LocalDateTime presentationTime = TestDataCreator.getLocalDateTimePlusHours(1);
        Weather weather = Weather.builder()
                .presentationTime(presentationTime)
                .location(location)
                .rain(1.0F)
                .rainProb(10)
                .temperature(14)
                .humidity(50)
                .windSpeed(1.5)
                .build();

        weatherRepository.save(weather);

        // when
        WeatherQueryResult.SimpleRainInfo simpleRainInfo = weatherRepository.getSimpleRainInfo(location);

        // thne
        assertThat(simpleRainInfo.getRainProbability()).isEqualTo(weather.getRainProb());
        assertThat(simpleRainInfo.getTime()).isEqualTo(presentationTime);
        assertThat(simpleRainInfo.getRain()).isEqualTo(weather.getRain());
    }

    @Test
    @Transactional
    void 간단_강수_정보_테스트_강수_확률이_없을_때() {
        // given
        Location location = locationRepository.save(createLocation());
        LocalDateTime presentationTime = TestDataCreator.getLocalDateTimePlusHours(1);
        Weather weather = Weather.builder()
                .presentationTime(presentationTime)
                .location(location)
                .rain(0.0F)
                .rainProb(0)
                .temperature(14)
                .humidity(50)
                .windSpeed(1.5)
                .build();

        weatherRepository.save(weather);

        // when
        WeatherQueryResult.SimpleRainInfo simpleRainInfo = weatherRepository.getSimpleRainInfo(location);

        // thne
        assertThat(simpleRainInfo).isNull();
    }


}