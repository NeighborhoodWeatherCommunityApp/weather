package org.pknu.weather.weather.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.weather.event.WeatherUpdateEvent;
import org.pknu.weather.repository.LocationRepository;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherRefresherServiceTest {

    @Mock
    LocationRepository locationRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;


    @InjectMocks
    WeatherRefresherService weatherRefresherService;

    @Test
    void 날씨_업데이트_이벤트를_발행할때_지역리스트가_null이면_아무행위도_하지않습니다() {
        // given
        doReturn(new ArrayList<Long>())
                .when(locationRepository).findLocationIdsWithRecentlyUpdatedWeather(any(Integer.class));

        // when
        weatherRefresherService.updateWeatherDataScheduled(100);

        // then
        verify(locationRepository, times(1)).findLocationIdsWithRecentlyUpdatedWeather(any(Integer.class));
        verify(eventPublisher, times(0)).publishEvent(any(WeatherUpdateEvent.class));
    }

    @Test
    void 날씨_업데이트_이벤트를_발행하는_반복문을_수행중에_예외가_발생할_경우() {
        // given
        List<Long> locationIds = List.of(1L, 2L, 3L);
        doReturn(locationIds)
                .when(locationRepository).findLocationIdsWithRecentlyUpdatedWeather(any(Integer.class));

        doNothing()
                .doThrow(new RuntimeException("두 번째 호출에서 예외"))
                .doNothing()
                .when(eventPublisher).publishEvent(any(WeatherUpdateEvent.class));

        // when
        weatherRefresherService.updateWeatherDataScheduled(100);

        // then
        verify(locationRepository, times(1)).findLocationIdsWithRecentlyUpdatedWeather(any(Integer.class));
        verify(eventPublisher, times(3)).publishEvent(any(WeatherUpdateEvent.class));
    }
}