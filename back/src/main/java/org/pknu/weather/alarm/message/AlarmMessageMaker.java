package org.pknu.weather.alarm.message;

import org.pknu.weather.alarm.sender.NotificationMessage;
import org.pknu.weather.alarm.dto.AlarmInfo;

public interface AlarmMessageMaker {
    NotificationMessage createAlarmMessage(AlarmInfo alarmInfo);
    void validate(AlarmInfo alarmInfo);
}
