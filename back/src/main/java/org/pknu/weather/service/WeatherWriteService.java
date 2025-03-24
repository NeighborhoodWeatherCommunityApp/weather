package org.pknu.weather.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.feignClient.utils.WeatherFeignClientUtils;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherWriteService {
    private final WeatherFeignClientUtils weatherFeignClientUtils;
    private final WeatherRepository weatherRepository;
    private final LocationRepository locationRepository;

    /**
     * 날씨 정보를 저장합니다. 비동기적으로 동작합니다.
     *
     * @param loc      member.getLocation()
     * @param forecast 공공데이터 API에서 받아온 단기날씨예보 값 list
     */
    @Transactional
    public void saveWeathersAsync(Location loc, List<Weather> forecast) {
        Location location = locationRepository.safeFindById(loc.getId());

        List<Weather> weatherList = new ArrayList<>(forecast).stream()
                .peek(weather -> weather.addLocation(location))
                .toList();

        weatherRepository.saveAll(weatherList);
    }

    /**
     * 단기 날씨 예보 API가 3시간 마다 갱신되기 때문에, 날씨 데이터 갱신을 위한 메서드
     *
     * @param locationId API를 호출한 사용자의 Location id
     * @return 해당 위치의 날씨 데이터 List
     */
    @Transactional
    public void updateWeathersAsync(Long locationId) {
        Location location = locationRepository.safeFindById(locationId);
        Map<LocalDateTime, Weather> oldWeatherMap = weatherRepository.findAllByLocationAfterNow(location);

        List<Weather> weatherList = weatherFeignClientUtils.getVillageShortTermForecast(location).stream()
                .peek(newWeather -> {
                    LocalDateTime presentationTime = newWeather.getPresentationTime();
                    if (oldWeatherMap.containsKey(presentationTime)) {
                        Weather oldWeather = oldWeatherMap.get(presentationTime);
                        oldWeather.updateWeather(newWeather);
                    } else {
                        weatherRepository.save(newWeather);
                    }
                })
                .toList();
    }
}
