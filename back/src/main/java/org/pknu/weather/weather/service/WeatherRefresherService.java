package org.pknu.weather.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.weather.ExtraWeather;
import org.pknu.weather.weather.dto.WeatherResponseDTO;
import org.pknu.weather.weather.event.WeatherCacheRefreshEvent;
import org.pknu.weather.weather.event.WeatherEvent;
import org.pknu.weather.weather.event.WeatherUpdateEvent;
import org.pknu.weather.weather.feignclient.utils.ExtraWeatherApiUtils;
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
            WeatherResponseDTO.ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                    toLocationDTO(location), extraWeather.getBasetime());
            extraWeather.updateExtraWeather(extraWeatherInfo);
            extraWeatherRepository.save(extraWeather);
        }
    }

    private void saveExtraWeather(Location location) {
        WeatherResponseDTO.ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                toLocationDTO(location));

        extraWeatherRepository.save(toExtraWeather(location, extraWeatherInfo));
    }

    /**
     * WeatherUpdateScheduler에 의해 스케쥴링으로 실행됩니다.
     */
    @Transactional(readOnly = true)
    public void updateWeatherDataScheduled(Integer limitSize) {
        List<Long> locationIdsWithRecentlyUpdatedWeather = locationRepository.findLocationIdsWithRecentlyUpdatedWeather(limitSize);
        for (Long locationId : locationIdsWithRecentlyUpdatedWeather) {
            publishEvent(new WeatherUpdateEvent(locationId));
        }
    }

    @Transactional(readOnly = true)
    public void updateWeatherCachedDataScheduled(Integer limitSize) {
        // TODO: 갱신 조건 고민 redis에서 zset으로 지역별 인기순 vs db에서 최근 갱신된 지역 인기순
        // 혹시 조건이 바뀔 수도 있어 위의 메서드와 분리
        List<Long> locationIdsWithRecentlyUpdatedWeather = locationRepository.findLocationIdsWithRecentlyUpdatedWeather(limitSize);
        for (Long locationId : locationIdsWithRecentlyUpdatedWeather) {
            publishEvent(new WeatherCacheRefreshEvent(locationId));
        }

        log.info("캐싱된 지역 수: {}, 지역 ids: {}", locationIdsWithRecentlyUpdatedWeather.size(), locationIdsWithRecentlyUpdatedWeather);
    }

    private void publishEvent(WeatherEvent event) {
        try {
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.warn("이벤트 처리 중 예외 발생. locationId: {}", event.getLocationId(), e);
        }
    }
}
