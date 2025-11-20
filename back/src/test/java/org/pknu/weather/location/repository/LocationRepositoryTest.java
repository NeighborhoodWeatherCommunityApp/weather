package org.pknu.weather.location.repository;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaRepositories(basePackageClasses = {
        org.pknu.weather.location.repository.LocationRepository.class,
        org.pknu.weather.weather.repository.WeatherRepository.class
})
@Import(DataJpaTestConfig.class)
class LocationRepositoryTest {
    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    LocationRepository locationRepository;

    @BeforeEach
    void init() {
        Location location = TestDataCreator.getBusanLocation();
        location = locationRepository.save(location);

        List<Weather> weatherList = new ArrayList<>();

        // 어제, 오늘 날씨 추가
        for (int i = 0; i <= 23; i++) {
            Weather weather = Weather.builder()
                    .basetime(LocalDateTime.now())
                    .presentationTime(
                            DateTimeFormatter.formattedDateTime2LocalDateTime(
                                    DateTimeFormatter.getFormattedLocalDate(LocalDate.now()),
                                    DateTimeFormatter.getFormattedTimeByOneHour(LocalTime.of(i, 0))))
                    .temperature(14)
                    .humidity(50)
                    .windSpeed(1.5)
                    .build();

            LocalDateTime now = LocalDateTime.now();
            Weather yesterday = Weather.builder()
                    .basetime(now)
                    .presentationTime(
                            DateTimeFormatter.formattedDateTime2LocalDateTime(
                                    DateTimeFormatter.getFormattedLocalDate(
                                            LocalDate.now().minusDays(1)),
                                    DateTimeFormatter.getFormattedTimeByOneHour(LocalTime.of(i, 0))))
                    .temperature(14)
                    .humidity(50)
                    .windSpeed(1.5)
                    .build();

            weather.addLocation(location);
            weatherList.add(weather);

            yesterday.addLocation(location);
            weatherList.add(yesterday);
        }

        weatherRepository.saveAll(weatherList);
    }

    @Test
    @Transactional
    @DisplayName("지역과 날씨를 페치조인 하는 테스트. 날씨는 미래의 날씨만 가져온다.")
    void locationAndWeatherFetchJoinTest() {
        // given
        Location location = locationRepository.findAll().get(0);

        // when
        List<Weather> weathers = weatherRepository.findAllWithLocation(location.getId(),
                LocalDateTime.now().plusHours(24));

        // then
        for (Weather weather : weathers) {
            assertThat(weather.getPresentationTime().isAfter(TestDataCreator.getLocalDateTime().withHour(0))).isTrue();
        }
    }
}