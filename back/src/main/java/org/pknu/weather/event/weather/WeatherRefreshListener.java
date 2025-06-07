package org.pknu.weather.event.weather;

import lombok.AllArgsConstructor;
import org.pknu.weather.service.WeatherService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class WeatherRefreshListener {
    private final WeatherService weatherService;
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WeatherCreateEvent event) {
        weatherService.bulkSaveWeathersAsync(event.getLocationId(), event.getNewForecast());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WeatherUpdateEvent event) {
        weatherService.bulkUpdateWeathersAsync(event.getLocationId());
    }
}
