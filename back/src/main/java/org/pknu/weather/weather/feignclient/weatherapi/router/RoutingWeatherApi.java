package org.pknu.weather.weather.feignclient.weatherapi.router;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.pknu.weather.weather.feignclient.weatherapi.type.WeatherApiType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class RoutingWeatherApi implements WeatherApi {
    private final Map<String, WeatherApi> weatherApiMap;
    private final WeatherApiType defaultApiType = WeatherApiType.KMA_API_HUB;

    @Override
    public List<Weather> getVillageShortTermForecast(Location location) {
        return weatherApiMap.get(defaultApiType.name()).getVillageShortTermForecast(location);
    }
}
