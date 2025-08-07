package org.pknu.weather.weather.event;

import lombok.Getter;

@Getter
public class WeatherCacheRefreshEvent implements WeatherEvent {
    private Long locationId;

    public WeatherCacheRefreshEvent(Long locationId) {
        this.locationId = locationId;
    }

    @Override
    public Long getLocationId() {
        return locationId;
    }
}
