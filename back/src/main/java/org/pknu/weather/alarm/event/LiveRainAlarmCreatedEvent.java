package org.pknu.weather.alarm.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LiveRainAlarmCreatedEvent implements AlarmTriggerEvent{
    private final long postId;
    private final LocalDateTime occurredTime = LocalDateTime.now();

    @Override
    public LocalDateTime getOccurredAt() {
        return this.occurredTime;
    }
}
