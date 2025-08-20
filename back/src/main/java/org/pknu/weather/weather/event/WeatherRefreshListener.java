package org.pknu.weather.weather.event;

import lombok.AllArgsConstructor;
import org.pknu.weather.weather.service.WeatherCacheService;
import org.pknu.weather.weather.service.WeatherService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class WeatherRefreshListener {
    private final WeatherService weatherService;
    private final WeatherCacheService weatherCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WeatherCreateEvent event) {
        weatherService.bulkSaveWeathersAsync(event.getLocationId(), event.getNewForecast());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WeatherUpdateEvent event) {
        weatherService.bulkUpdateWeathersAsync(event.getLocationId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handel(WeatherCacheRefreshEvent event) {
        weatherCacheService.updateCachedWeathersForLocation(event.getLocationId());
    }
}
