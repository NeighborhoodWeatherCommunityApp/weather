package org.pknu.weather.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.converter.WeatherResponseConverter;
import org.pknu.weather.weather.dto.WeatherQueryResultDTO;
import org.pknu.weather.weather.dto.WeatherResponseDTO;
import org.pknu.weather.weather.event.WeatherCreateEvent;
import org.pknu.weather.weather.feignclient.utils.WeatherFeignClientUtils;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableCaching
@Slf4j
@Transactional(readOnly = true)
public class WeatherQueryService {
    private final MemberRepository memberRepository;
    private final WeatherRepository weatherRepository;
    private final WeatherFeignClientUtils weatherFeignClientUtils;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cm;
    private final String LOCATION_UPDATE_STORE = "locationUpdateStore";
    private final String LOCATION_CREATE_STORE = "locationCreateStore";

    /**
     * TODO: 성능 개선 필요
     * 현재 ~ +24시간 까지의 날씨 정보를 불러옵니다.
     *
     * @param locationId
     * @return
     */
    public List<Weather> getWeathers(Long locationId) {
        return weatherRepository.findAllWithLocation(locationId, LocalDateTime.now().plusHours(24)).stream()
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList();
    }

    public Weather getNearestWeatherForecastToNow(Location location) {
        Optional<Weather> optionalWeather = weatherRepository.findWeatherByClosestPresentationTime(location);

        if (optionalWeather.isEmpty()) {
            List<Weather> newForecast = weatherFeignClientUtils.getVillageShortTermForecast(location);
            eventPublisher.publishEvent(new WeatherCreateEvent(location.getId(), newForecast));
            return newForecast.get(0);
        }

        return optionalWeather.get();
    }

    public WeatherResponseDTO.SimpleRainInformation getSimpleRainInfo(String email) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = member.getLocation();
        WeatherQueryResultDTO.SimpleRainInfo simpleRainInfo = weatherRepository.getSimpleRainInfo(location);
        return WeatherResponseConverter.toSimpleRainInformation(simpleRainInfo);
    }

    /**
     * 해당 지역의 날씨가 갱신되었는지 확인하는 메서드
     *
     * @param location
     * @return true = 갱신되었음(3시간 안지남), false = 갱신되지 않았음(3시간 지남)
     */
    public boolean weatherHasBeenUpdated(Location location) {
        Cache cache = cm.getCache(LOCATION_UPDATE_STORE);
        if (cacheExist(cache, location.getId())) {
            cache.put(location.getId(), true);
            return true;
        }

        boolean value = weatherRepository.weatherHasBeenUpdated(location);
        if (value) {
            cache.put(location.getId(), true);
        }

        return value;
    }


    /**
     * 해당 지역의 날씨 데이터가 존재하는지 확인하는 메서드
     *
     * @param location
     * @return true = 존재함, false = 존재 하지 않음
     */
    public boolean weatherHasBeenCreated(Location location) {
        Cache cache = cm.getCache(LOCATION_CREATE_STORE);
        if (cacheExist(cache, location.getId())) {
            cache.put(location.getId(), true);
            return true;
        }

        boolean value = weatherRepository.weatherHasBeenCreated(location);
        if (value) {
            cache.put(location.getId(), true);
        }

        return value;
    }

    private boolean cacheExist(Cache cache, Long locationId) {
        return cache != null &&
                Optional.ofNullable(cache.get(locationId))
                        .map(Cache.ValueWrapper::get)
                        .map(Boolean.class::cast)
                        .orElse(false);
    }
}
