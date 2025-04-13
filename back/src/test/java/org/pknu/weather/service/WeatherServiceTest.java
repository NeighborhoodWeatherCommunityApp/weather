package org.pknu.weather.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.domain.common.SkyType;
import org.pknu.weather.feignClient.utils.WeatherFeignClientUtils;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@Slf4j
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {
    @Autowired
    WeatherService weatherService;

    @Autowired
    LocationRepository locationRepository;

    @SpyBean
    WeatherFeignClientUtils weatherFeignClientUtils;

    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    EntityManager em;

    Map<LocalDateTime, Weather> getPastForecast(Location location, LocalDateTime targetTime) {
        // 현재 시각
        LocalDateTime baseTime = targetTime.minusHours(3);
        Map<LocalDateTime, Weather> weatherMap = new HashMap<>();

        // 3시간 전에 발표한 예보 만들기
        for(int i = 1; i < 24; i++) {
            Weather weather = Weather.builder()
                    .basetime(baseTime)
                    .presentationTime(targetTime.plusHours(i))
                    .location(location)
                    .rainType(RainType.NONE)
                    .rain(1.0F)
                    .rainProb(10)
                    .temperature(14)
                    .humidity(50)
                    .windSpeed(1.5)
                    .snowCover(1.5f)
                    .skyType(SkyType.CLEAR)
                    .build();

            weatherMap.put(weather.getPresentationTime(), weather);
        }

        return weatherMap;
    }

    List<Weather> getNewForecast(Location location, LocalDateTime targetTime) {
        // 현재 시각
        LocalDateTime baseTime = targetTime;
        List<Weather> weatherList = new ArrayList<>();

        // 3시간 전에 발표한 예보 만들기
        for(int i = 1; i < 24; i++) {
            Weather weather = Weather.builder()
                    .basetime(baseTime)
                    .presentationTime(targetTime.plusHours(i))
                    .location(location)
                    .rainType(RainType.NONE)
                    .rain(1.0F)
                    .rainProb(10)
                    .temperature(14)
                    .humidity(50)
                    .windSpeed(1.5)
                    .snowCover(1.5f)
                    .skyType(SkyType.CLEAR)
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
                .when(weatherFeignClientUtils)
                .getVillageShortTermForecast(location);

//        when(weatherFeignClientUtils.getVillageShortTermForecast(location))
//                .thenReturn(getNewForecast(location, now));

        weatherRepository.saveAll(getPastForecast(location, now).values());

        // when
        weatherService.updateWeathersAsync(location.getId());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        List<Weather> updatedWeatherList = weatherRepository.findAllByLocationAfterNow(location).values().stream()
                .toList();

        updatedWeatherList.stream()
                        .forEach(weather -> {
                            assertThat(weather.getBasetime()).isEqualTo(now);
                        });
    }

    @AfterEach
    void remove() {
        locationRepository.deleteAll();
        weatherRepository.deleteAll();
    }
}
