package org.pknu.weather.service.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.dto.AlarmMemberDTO;
import org.pknu.weather.dto.ExtraWeatherSummaryDTO;
import org.pknu.weather.dto.WeatherSummaryDTO;

@Getter
@Builder
@RequiredArgsConstructor
public class WeatherSummaryAlarmInfo implements AlarmInfo{
    private final WeatherSummaryDTO weatherSummaryDTO;
    private final ExtraWeatherSummaryDTO extraWeatherSummaryDTO;
    private final AlarmMemberDTO alarmMemberDTO;
}
