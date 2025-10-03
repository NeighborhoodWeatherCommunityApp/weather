package org.pknu.weather.event.alarm;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.service.AlarmService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AlarmEventListener {

    private final AlarmService alarmService;

    @Async("AlarmExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LiveRainAlarmCreatedEvent event) {
        alarmService.trigger(AlarmType.RAIN_ALERT, event);
    }

}
