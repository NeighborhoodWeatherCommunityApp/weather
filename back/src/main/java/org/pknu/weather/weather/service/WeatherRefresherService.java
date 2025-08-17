package org.pknu.weather.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.weather.event.WeatherUpdateEvent;
import org.pknu.weather.weather.feignclient.utils.ExtraWeatherApiUtils;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.weather.ExtraWeather;
import org.pknu.weather.weather.dto.WeatherResponse.ExtraWeatherInfo;
import org.pknu.weather.weather.repository.ExtraWeatherRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.pknu.weather.location.converter.LocationConverter.toLocationDTO;
import static org.pknu.weather.weather.converter.ExtraWeatherConverter.toExtraWeather;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeatherRefresherService {

    private final LocationRepository locationRepository;
    private final ExtraWeatherRepository extraWeatherRepository;
    private final ExtraWeatherApiUtils extraWeatherApiUtils;
    private final WeatherQueryService weatherQueryService;
    private final WeatherService weatherService;
    private final ApplicationEventPublisher eventPublisher;

    public void refresh(Set<Long> locationIds) {
        List<Location> locations = locationRepository.findByIdIn(locationIds);
        for (Location location : locations) {
            updateWeather(location);
            updateExtraWeather(location);
        }
    }

    private void updateWeather(Location location) {

        if (!weatherQueryService.weatherHasBeenCreated(location)) {
            weatherService.saveWeathers(location);
        }

        if (!weatherQueryService.weatherHasBeenUpdated(location)) {
            weatherService.updateWeathers(location.getId());
        }
    }

    public void updateExtraWeather(Location location) {
        extraWeatherRepository.findByLocationId(location.getId())
                .ifPresentOrElse(
                        extraWeather -> updateExistingExtraWeather(location, extraWeather),
                        () -> saveExtraWeather(location)
                );
    }

    private void updateExistingExtraWeather(Location location, ExtraWeather extraWeather) {
        if (extraWeather.getBasetime().isBefore(LocalDateTime.now().minusHours(3))) {
            ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                    toLocationDTO(location), extraWeather.getBasetime());
            extraWeather.updateExtraWeather(extraWeatherInfo);
            extraWeatherRepository.save(extraWeather);
        }
    }

    private void saveExtraWeather(Location location) {
        ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                toLocationDTO(location));

        extraWeatherRepository.save(toExtraWeather(location, extraWeatherInfo));
    }

    /**
     * WeatherUpdateScheduler에 의해 스케쥴링으로 실행됩니다.
     */
    public void updateWeatherDataScheduled(Integer limitSize) {
        List<Long> locationIdsWithRecentlyUpdatedWeather = locationRepository.findLocationIdsWithRecentlyUpdatedWeather(limitSize);
        for (Long locationId : locationIdsWithRecentlyUpdatedWeather) {
            publishEvent(locationId);
        }
    }

    private void publishEvent(Long locationId) {
        try {
            eventPublisher.publishEvent(new WeatherUpdateEvent(locationId));
        } catch (Exception e) {
            log.warn("이벤트 처리 중 예외 발생. locationId: {}", locationId, e);
        }
    }
}
