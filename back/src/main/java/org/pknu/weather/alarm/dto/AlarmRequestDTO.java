package org.pknu.weather.alarm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pknu.weather.alarm.enums.SummaryAlarmTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRequestDTO {

    @NotBlank(message = "FCM 토큰은 비어있을 수 없습니다.")
    @Size(max = 255, message = "FCM 토큰 길이가 너무 깁니다.")
    private String fcmToken;

    private Boolean agreeTempAlarm;
    private Boolean agreePrecipAlarm;
    private Boolean agreeDustAlarm;
    private Boolean agreeUvAlarm;
    private Boolean agreeLiveRainAlarm;
    private Set<SummaryAlarmTime> summaryAlarmTimes;

}
