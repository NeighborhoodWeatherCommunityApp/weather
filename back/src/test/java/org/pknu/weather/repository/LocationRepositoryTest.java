package org.pknu.weather.repository;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class LocationRepositoryTest {

    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    LocationRepository locationRepository;

    private final double LATITUDE = 35.1845361111111;
    private final double LONGITUDE = 128.989688888888;

    @BeforeEach
    void init() {
        Location location = Location.builder()
                .point(GeometryUtils.getPoint(LATITUDE, LONGITUDE))
                .city("city")
                .province("province")
                .street("street")
                .latitude(30.0)
                .longitude(60.0)
                .build();

        List<Weather> weatherList = new ArrayList<>();

        // 어제, 오늘 날씨 추가
        for(int i = 0; i <= 23; i++) {
            Weather weather = Weather.builder()
                    .basetime(LocalDateTime.now())
                    .presentationTime(
                            DateTimeFormatter.formattedDateTime2LocalDateTime(DateTimeFormatter.getFormattedDate(),
                            DateTimeFormatter.getFormattedTimeByOneHour(LocalTime.of(i, 0))))
                    .build();

            LocalDateTime now = LocalDateTime.now();
            Weather yesterday = Weather.builder()
                    .basetime(now)
                    .presentationTime(
                            DateTimeFormatter.formattedDateTime2LocalDateTime(
                                    DateTimeFormatter.getFormattedDate(LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth() - 1)),
                                    DateTimeFormatter.getFormattedTimeByOneHour(LocalTime.of(i, 0))))
                    .build();

            weather.addLocation(location);
            weatherList.add(weather);

            yesterday.addLocation(location);
            weatherList.add(yesterday);
        }

        System.out.println("##################"+weatherList.size());
        weatherRepository.saveAll(weatherList);
    }

    @Test
    @Transactional
    @DisplayName("지역과 날씨를 페치조인 하는 테스트. 날씨는 미래의 날씨만 가져온다.")
    void locationAndWeatherFetchJoinTest() {
        // given
        Location location = locationRepository.findAll().get(0);
        int eq = 24 - LocalTime.now().getHour() - 1;

        // when
        List<Weather> weathers = weatherRepository.findAllWithLocation(location, LocalDateTime.now().plusHours(24));

        // then
        assertThat(weathers.size()).isEqualTo(eq);
        assertThat(weathers.get(0).getLocation().getId()).isEqualTo(location.getId());
    }
}
