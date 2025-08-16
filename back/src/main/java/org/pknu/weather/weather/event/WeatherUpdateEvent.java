package org.pknu.weather.weather.event;

import lombok.Getter;

@Getter
public class WeatherUpdateEvent {
    private Long locationId;
    public WeatherUpdateEvent(Long locationId) {
        this.locationId = locationId;
    }
}
