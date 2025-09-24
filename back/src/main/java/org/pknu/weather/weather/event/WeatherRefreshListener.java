package org.pknu.weather.weather.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.weather.service.WeatherCacheService;
import org.pknu.weather.weather.service.WeatherService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
@Slf4j
public class WeatherRefreshListener {
    private final WeatherService weatherService;
    private final WeatherCacheService weatherCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WeatherCreateEvent event) {
        weatherService.bulkSaveWeathersAsync(event.getLocationId(), event.getNewForecast());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WeatherUpdateEvent event) {
        log.info("bulk update 이벤트 발행 locationId: {}", event.getLocationId());
        weatherService.bulkUpdateWeathersAsync(event.getLocationId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handel(WeatherCacheRefreshEvent event) {
        log.info("캐시 refresh 이벤트 발행 locationId: {}", event.getLocationId());
        weatherCacheService.updateCachedWeathersForLocation(event.getLocationId());
    }
}
