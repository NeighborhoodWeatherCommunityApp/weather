package org.pknu.weather.alarm.sender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pknu.weather.alarm.message.WeatherAlarmMessageBuilder;

@Getter
@AllArgsConstructor
public class FcmMessage extends WeatherAlarmMessageBuilder implements NotificationMessage {
    private String fcmToken;
    private String alarmTitle;
    private String alarmMessage;
}
