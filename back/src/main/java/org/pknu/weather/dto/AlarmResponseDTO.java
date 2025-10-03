package org.pknu.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pknu.weather.alarm.enums.SummaryAlarmTime;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmResponseDTO {

    private Boolean agreeTempAlarm;
    private Boolean agreePrecipAlarm;
    private Boolean agreeDustAlarm;
    private Boolean agreeUvAlarm;
    private Boolean agreeLiveRainAlarm;
    private Set<SummaryAlarmTime> summaryAlarmTimes;

}
