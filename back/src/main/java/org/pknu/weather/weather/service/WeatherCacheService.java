package org.pknu.weather.weather.service;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.converter.WeatherConverter;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.pknu.weather.weather.repository.WeatherRedisRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherCacheService {
    private final WeatherRedisRepository weatherRedisRepository;
    private final WeatherApi WeatherApi;
    private final LocationRepository locationRepository;

    public List<Weather> getCachedWeathers(Long locationId) {
        List<WeatherRedisDTO.WeatherData> weathers = weatherRedisRepository.getWeatherList(locationId);
        return WeatherConverter.toWeatherList(weathers);
    }

    @Async("WeatherCUDExecutor")
    public void updateCachedWeathersForLocation(Long locationId) {
        Location location = locationRepository.safeFindById(locationId);
        List<Weather> weatherList = WeatherApi.getVillageShortTermForecast(location);
        List<WeatherRedisDTO.WeatherData> weatherDataList = WeatherConverter.toWeatherDataList(weatherList);
        weatherRedisRepository.updateWeatherList(locationId, weatherDataList);
    }
}
