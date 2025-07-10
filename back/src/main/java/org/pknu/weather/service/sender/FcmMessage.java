package org.pknu.weather.service.sender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pknu.weather.service.message.WeatherAlarmMessageBuilder;

@Getter
@AllArgsConstructor
public class FcmMessage extends WeatherAlarmMessageBuilder implements NotificationMessage {
    private String fcmToken;
    private String alarmTitle;
    private String alarmMessage;
}
