package org.pknu.weather.weather.feignclient.weatherapi.target;

import java.util.List;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.weather.Weather;

public interface WeatherApi {
    List<Weather> getVillageShortTermForecast(Location location);
}
