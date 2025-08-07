package org.pknu.weather.weather.event;

import lombok.Getter;

@Getter
public class WeatherUpdateEvent implements WeatherEvent {
    private Long locationId;

    public WeatherUpdateEvent(Long locationId) {
        this.locationId = locationId;
    }

    @Override
    public Long getLocationId() {
        return locationId;
    }
}
