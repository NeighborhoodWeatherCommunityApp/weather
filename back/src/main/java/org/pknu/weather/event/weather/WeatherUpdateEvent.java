package org.pknu.weather.event.weather;

import lombok.Getter;

@Getter
public class WeatherUpdateEvent {
    private Long locationId;
    public WeatherUpdateEvent(Long locationId) {
        this.locationId = locationId;
    }
}
