package org.pknu.weather.weather.service;

import static org.mockito.Mockito.doReturn;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
@Slf4j
class WeatherServiceTest {
    @Autowired
    WeatherService weatherService;

    @Autowired
    LocationRepository locationRepository;

    @SpyBean
    WeatherApi weatherApi;

    @Autowired
    WeatherRepository weatherRepository;

    @Test
    void 비동기_insert_로직_테스트() {
        // given
        Location location = locationRepository.saveAndFlush(TestDataCreator.getBusanLocation());
        LocalDateTime baseTime = TestDataCreator.getBaseTime();

        // when
        weatherService.saveWeathersAsync(location.getId(), TestDataCreator.getNewForecast(location, baseTime));

        // then
        Awaitility.await().until(() -> weatherRepository.count() == 24);
        List<Weather> weatherList = weatherRepository.findAll();
        Assertions.assertThat(weatherList.size()).isEqualTo(24);
    }

    @Test
    void 비동기_벌크_insert_로직_테스트() {
        // given
        Location location = locationRepository.saveAndFlush(TestDataCreator.getBusanLocation());
        LocalDateTime baseTime = TestDataCreator.getBaseTime();

        // when
        weatherService.bulkSaveWeathersAsync(location.getId(), TestDataCreator.getNewForecast(location, baseTime));

        // then
        Awaitility.await().until(() -> weatherRepository.count() == 24);
        List<Weather> weatherList = weatherRepository.findAll();
        Assertions.assertThat(weatherList.size()).isEqualTo(24);
    }

    //    @Test
    void 비동기_벌크_update_로직_테스트() throws InterruptedException {
        // given
        Location location = locationRepository.saveAndFlush(TestDataCreator.getBusanLocation());
        LocalDateTime baseTime = TestDataCreator.getBaseTime();
        weatherRepository.saveAll(TestDataCreator.getPastForecast(location, baseTime.minusHours(3)).values());

        doReturn(TestDataCreator.getNewForecast(location, baseTime))
                .when(weatherApi).getVillageShortTermForecast(location);

        // when
        weatherService.bulkUpdateWeathersAsync(location.getId());

        // then
        Awaitility.await().until(() -> weatherRepository.count() == 27);
        List<Weather> weatherList = weatherRepository.findAll();
        Assertions.assertThat(weatherList.size()).isEqualTo(27);
    }

    @AfterEach
    void remove() {
        weatherRepository.deleteAll();
        locationRepository.deleteAll();
    }
}