package org.pknu.weather.alarm.utils;

import java.time.LocalTime;
import org.pknu.weather.alarm.enums.SummaryAlarmTime;

public class AlarmTimeUtil {
    public static SummaryAlarmTime getCurrentAlarmTime() {
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(10, 0))) {
            return SummaryAlarmTime.MORNING;
        } else if (now.isBefore(LocalTime.of(15, 0))) {
            return SummaryAlarmTime.AFTERNOON;
        } else {
            return SummaryAlarmTime.EVENING;
        }
    }
}