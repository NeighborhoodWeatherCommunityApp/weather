package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.Alarm;
import org.pknu.weather.dto.AlarmResponseDTO;

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
