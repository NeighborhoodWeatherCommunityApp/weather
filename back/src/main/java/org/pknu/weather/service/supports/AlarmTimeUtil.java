package org.pknu.weather.service.supports;

import java.time.LocalTime;
import org.pknu.weather.domain.common.SummaryAlarmTime;

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