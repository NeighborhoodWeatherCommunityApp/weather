package org.pknu.weather.service;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.domain.Location;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.pknu.weather.weather.service.WeatherQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootTest
@EnableCaching
class WeatherQueryServiceTest {
    @Autowired
    private WeatherQueryService weatherQueryService;

    @MockBean
    private WeatherRepository weatherRepository;

    @Test
    void weatherHasBeenUpdated() {
        Location location = Location.builder().id(1L).build();
        when(weatherRepository.weatherHasBeenUpdated(location))
                .thenReturn(false)
                .thenReturn(true);

        boolean val1 = weatherQueryService.weatherHasBeenUpdated(location);
        Assertions.assertThat(val1).isFalse();

        boolean val2 = weatherQueryService.weatherHasBeenUpdated(location);
        Assertions.assertThat(val2).isTrue();

        boolean val3 = weatherQueryService.weatherHasBeenUpdated(location);
        Assertions.assertThat(val3).isTrue();
    }
}