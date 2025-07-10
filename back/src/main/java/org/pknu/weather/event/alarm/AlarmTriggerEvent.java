package org.pknu.weather.event.alarm;

import java.time.LocalDateTime;


public interface AlarmTriggerEvent {
    LocalDateTime getOccurredAt();
}
