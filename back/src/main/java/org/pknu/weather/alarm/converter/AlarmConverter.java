package org.pknu.weather.alarm.converter;

import org.pknu.weather.alarm.entity.Alarm;
import org.pknu.weather.alarm.dto.AlarmResponseDTO;

public class AlarmConverter {

    public static AlarmResponseDTO toAlarmResponseDto(Alarm alarm) {
        return AlarmResponseDTO.builder()
                .agreeUvAlarm(alarm.getAgreeUvAlarm())
                .agreeTempAlarm(alarm.getAgreeTempAlarm())
                .agreePrecipAlarm(alarm.getAgreePrecipAlarm())
                .agreeDustAlarm(alarm.getAgreeDustAlarm())
                .agreeLiveRainAlarm(alarm.getAgreeLiveRainAlarm())
                .summaryAlarmTimes(alarm.getSummaryAlarmTimes())
                .build();
    }
}
