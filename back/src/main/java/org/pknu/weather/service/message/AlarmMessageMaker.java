package org.pknu.weather.service.message;

import org.pknu.weather.service.sender.NotificationMessage;
import org.pknu.weather.service.dto.AlarmInfo;

public interface AlarmMessageMaker {
    NotificationMessage createAlarmMessage(AlarmInfo alarmInfo);
    void validate(AlarmInfo alarmInfo);
}
