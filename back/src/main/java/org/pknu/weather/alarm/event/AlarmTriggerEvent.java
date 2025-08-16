package org.pknu.weather.alarm.event;

import java.time.LocalDateTime;


public interface AlarmTriggerEvent {
    LocalDateTime getOccurredAt();
}
