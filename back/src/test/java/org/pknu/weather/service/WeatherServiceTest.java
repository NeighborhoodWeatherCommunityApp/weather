package org.pknu.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.domain.common.SkyType;
import org.pknu.weather.feignClient.utils.WeatherFeignClientUtils;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootTest
@Slf4j
@Import(TestAsyncConfig.class)
class WeatherServiceTest {
    @Autowired
    WeatherService weatherService;

    @Autowired
    LocationRepository locationRepository;

    @SpyBean
    WeatherFeignClientUtils weatherFeignClientUtils;

    @Autowired
    WeatherRepository weatherRepository;

    Map<LocalDateTime, Weather> getPastForecast(Location location, LocalDateTime targetTime) {
        // 현재 시각
        LocalDateTime baseTime = DateTimeFormatter.getBaseLocalDateTime(targetTime.minusHours(3));
        targetTime = LocalDateTime.of(baseTime.getYear(), baseTime.getMonth(), baseTime.getDayOfMonth(),
                baseTime.getHour(), 0);
        Map<LocalDateTime, Weather> weatherMap = new HashMap<>();

        // 3시간 전에 발표한 예보 만들기
        for (int i = 1; i <= 24; i++) {
            Weather weather = Weather.builder()
                    .basetime(baseTime)
                    .presentationTime(targetTime.plusHours(i))
                    .location(location)
                    .rainType(RainType.values()[(int) (Math.random() * RainType.values().length)])
                    .rain((float) (Math.random() * 10 + i))
                    .rainProb((int) (Math.random() * 100))
                    .temperature((int) (Math.random() * 30 + i))
                    .humidity((int) (Math.random() * 100))
                    .windSpeed(Math.random() * 10 + i)
                    .snowCover((float) (Math.random() * 5 + i))
                    .skyType(SkyType.values()[(int) (Math.random() * SkyType.values().length)])
                    .build();

            weatherMap.put(weather.getPresentationTime(), weather);
        }

        return weatherMap;
    }

    List<Weather> getNewForecast(Location location, LocalDateTime targetTime) {
        // 현재 시각
        LocalDateTime baseTime = DateTimeFormatter.getBaseLocalDateTime(targetTime);
        targetTime = LocalDateTime.of(baseTime.getYear(), baseTime.getMonth(), baseTime.getDayOfMonth(),
                baseTime.getHour(), 0);
        List<Weather> weatherList = new ArrayList<>();

        // 3시간 전에 발표한 예보 만들기
        for (int i = 1; i <= 24; i++) {
            Weather weather = Weather.builder()
                    .basetime(baseTime)
                    .presentationTime(targetTime.plusHours(i))
                    .location(location)
                    .rainType(RainType.values()[(int) (Math.random() * RainType.values().length)])
                    .rain((float) (Math.random() * 10 + i))
                    .rainProb((int) (Math.random() * 100))
                    .temperature((int) (Math.random() * 30 + i))
                    .humidity((int) (Math.random() * 100))
                    .windSpeed(Math.random() * 10 + i)
                    .snowCover((float) (Math.random() * 5 + i))
                    .skyType(SkyType.values()[(int) (Math.random() * SkyType.values().length)])
                    .build();

            weatherList.add(weather);
        }

        return weatherList;
    }

    @Test
    void 단기_예보_갱신_성공테스트() {
        // given
        Location location = locationRepository.saveAndFlush(TestDataCreator.getBusanLocation());
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        doReturn(getNewForecast(location, now))
                .when(weatherFeignClientUtils).getVillageShortTermForecast(location);

        weatherRepository.saveAll(getPastForecast(location, now).values());

        // when
        weatherService.updateWeathersAsync(location.getId());

        // then
        List<Weather> updatedWeatherList = weatherRepository.findAllByLocationAfterNow(location).values().stream()
                .toList();

        updatedWeatherList.stream()
                .forEach(weather -> {
                    assertThat(weather.getBasetime()).isEqualTo(now);
                });
    }

    @Test
    void 비동기_insert_로직_테스트() throws InterruptedException {
        // given
        Location location = locationRepository.saveAndFlush(TestDataCreator.getBusanLocation());
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        // when
        weatherService.saveWeathersAsync(location.getId(), getNewForecast(location, now));

        // then
        List<Weather> weatherList = weatherRepository.findAll();
        Assertions.assertThat(weatherList.size()).isEqualTo(24);
    }

    @Test
    void 비동기_벌크_insert_로직_테스트() throws InterruptedException {
        // given
        Location location = locationRepository.saveAndFlush(TestDataCreator.getBusanLocation());
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        // when
        weatherService.bulkSaveWeathersAsync(location.getId(), getNewForecast(location, now));

        // then
        List<Weather> weatherList = weatherRepository.findAll();
        Assertions.assertThat(weatherList.size()).isEqualTo(24);
    }

    @Test
    void 비동기_update_로직_테스트() throws InterruptedException {
        // given
        Location location = locationRepository.saveAndFlush(TestDataCreator.getBusanLocation());
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        weatherRepository.saveAll(getPastForecast(location, now).values());
        doReturn(getNewForecast(location, now))
                .when(weatherFeignClientUtils).getVillageShortTermForecast(location);

        // when
        weatherService.updateWeathersAsync(location.getId());

        // then
        List<Weather> weatherList = weatherRepository.findAll();
        Assertions.assertThat(weatherList.size()).isEqualTo(27);
    }

    @Test
    void 비동기_벌크_update_로직_테스트() throws InterruptedException {
        // given
        Location location = locationRepository.saveAndFlush(TestDataCreator.getBusanLocation());
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        weatherRepository.saveAll(getPastForecast(location, now).values());
        doReturn(getNewForecast(location, now))
                .when(weatherFeignClientUtils).getVillageShortTermForecast(location);

        // when
        weatherService.bulkUpdateWeathersAsync(location.getId());

        // then
        List<Weather> weatherList = weatherRepository.findAll();
        Assertions.assertThat(weatherList.size()).isEqualTo(27);
    }

    @AfterEach
    void remove() {
        weatherRepository.deleteAll();
        locationRepository.deleteAll();
    }
}

@TestConfiguration
@EnableAsync
class TestAsyncConfig {

    @Bean(name = "WeatherCUDExecutor")
    @Primary
    public Executor testWeatherExecutor() {
        return new SyncTaskExecutor();
    }

    @Primary
    @Bean(name = "ExpCUDExecutor")
    public Executor testExpExecutor() {
        return new SyncTaskExecutor();
    }
}
