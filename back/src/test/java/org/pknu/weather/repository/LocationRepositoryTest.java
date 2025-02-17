package org.pknu.weather.repository;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.config.TestConfig;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
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
                            DateTimeFormatter.formattedDateTime2LocalDateTime(DateTimeFormatter.getFormattedBaseDate(),
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
                                    DateTimeFormatter.getFormattedBaseDate(
                                            LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth() - 1)),
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
        List<Weather> weathers = weatherRepository.findAllWithLocation(location, LocalDateTime.now().plusHours(24));

        // then
        for (Weather weather : weathers) {
            assertThat(weather.getPresentationTime().isAfter(TestDataCreator.getLocalDateTime().withHour(0))).isTrue();
        }
    }
}