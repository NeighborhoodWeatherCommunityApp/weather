package org.pknu.weather.alarm.scheduler;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.alarm.enums.AlarmType;
import org.pknu.weather.alarm.service.AlarmService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmScheduler {

    private final AlarmService alarmService;

    // 날씨 데이터 업데이트
    @Scheduled(cron = "0 40 6,11,17 * * *")
    public void runWeatherUpdateTask() { alarmService.trigger(AlarmType.WEATHER_UPDATE); }

    // 날씨 요약 알림 전송
    @Scheduled(cron = "0 0 7,12,18 * * *")
    public void runWeatherSummaryAlarm() {
        alarmService.trigger(AlarmType.WEATHER_SUMMARY);
    }
}