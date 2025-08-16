package org.pknu.weather.weather.event;

import lombok.Getter;
import org.pknu.weather.weather.Weather;

import java.util.List;

@Getter
public class WeatherCreateEvent implements WeatherEvent {
    private Long locationId;
    private List<Weather> newForecast;

    public WeatherCreateEvent(Long locationId, List<Weather> newForecast) {
        this.locationId = locationId;
        this.newForecast = newForecast;
    }

    @Override
    public Long getLocationId() {
        return locationId;
    }
}
