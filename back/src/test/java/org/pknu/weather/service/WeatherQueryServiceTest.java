package org.pknu.weather.service;

import org.junit.jupiter.api.Test;
import org.pknu.weather.domain.Location;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;

import static org.mockito.Mockito.*;

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
        when(weatherRepository.weatherHasBeenUpdated(location)).thenReturn(true);

        weatherQueryService.weatherHasBeenUpdated(location);
        weatherQueryService.weatherHasBeenUpdated(location);

        verify(weatherRepository, times(1)).weatherHasBeenUpdated(location);
    }
}