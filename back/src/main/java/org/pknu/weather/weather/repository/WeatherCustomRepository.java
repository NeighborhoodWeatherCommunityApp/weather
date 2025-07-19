package org.pknu.weather.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.dto.WeatherQueryResult;
import org.pknu.weather.weather.dto.WeatherSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface WeatherCustomRepository {
    boolean weatherHasBeenUpdated(Location location);

    boolean weatherHasBeenCreated(Location location);

    WeatherQueryResult.SimpleRainInfo getSimpleRainInfo(Location locationEntity);

    Optional<Weather> findWeatherByClosestPresentationTime(Location location);

    Map<LocalDateTime, Weather> findAllByLocationAfterNow(Location location);

    List<WeatherSummaryDTO> findWeatherSummary(Set<Long> locationIds);

    void batchUpdate(List<Weather> weatherList, Location location);

    void batchSave(List<Weather> newForecast, Location location);
}
