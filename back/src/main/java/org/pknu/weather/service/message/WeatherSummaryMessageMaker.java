package org.pknu.weather.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.service.dto.AlarmInfo;
import org.pknu.weather.dto.AlarmMemberDTO;
import org.pknu.weather.service.sender.FcmMessage;
import org.pknu.weather.dto.ExtraWeatherSummaryDTO;
import org.pknu.weather.service.dto.WeatherSummaryAlarmInfo;
import org.pknu.weather.dto.WeatherSummaryDTO;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherSummaryMessageMaker implements AlarmMessageMaker {

    @Override
    public FcmMessage createAlarmMessage(AlarmInfo alarmInfo) {

        validate(alarmInfo);

        WeatherSummaryAlarmInfo weatherSummaryAlarmInfo = (WeatherSummaryAlarmInfo) alarmInfo;

        return createWeatherSummaryAlarm(weatherSummaryAlarmInfo.getWeatherSummaryDTO(),
                weatherSummaryAlarmInfo.getExtraWeatherSummaryDTO(),
                weatherSummaryAlarmInfo.getAlarmMemberDTO());
    }

    private FcmMessage createWeatherSummaryAlarm(WeatherSummaryDTO summaryDTO,
                                                       ExtraWeatherSummaryDTO extraDTO, AlarmMemberDTO alarmMemberDTO) {

        WeatherAlarmMessageBuilder weatherAlarmMessageBuilder = new WeatherAlarmMessageBuilder();

        if (alarmMemberDTO.getAgreePrecipAlarm())
            weatherAlarmMessageBuilder.withRainStatus(summaryDTO.getRainStatus());

        if (alarmMemberDTO.getAgreeTempAlarm())
            weatherAlarmMessageBuilder.withTemperature(summaryDTO.getMaxTemp(), summaryDTO.getMinTemp());

        if (alarmMemberDTO.getAgreeUvAlarm())
            weatherAlarmMessageBuilder.withUV(extraDTO.getMaxUvTime(), extraDTO.getMaxUvValue());

        if (alarmMemberDTO.getAgreeDustAlarm())
            weatherAlarmMessageBuilder.withDust(extraDTO.getPm10());

        String messageBody = weatherAlarmMessageBuilder.build();

        return new FcmMessage(alarmMemberDTO.getFcmToken(), AlarmType.WEATHER_SUMMARY.getDescription(), messageBody);
    }
    @Override
    public void validate(AlarmInfo alarmInfo) {
        if (!(alarmInfo instanceof WeatherSummaryAlarmInfo weatherInfo)) {
            log.warn("유효성 검사 실패: WeatherSummaryMessageMaker는 {} 타입만 처리 가능. 입력 타입: {}",
                    WeatherSummaryAlarmInfo.class.getSimpleName(),
                    alarmInfo != null ? alarmInfo.getClass().getName() : "null");
            throw new IllegalArgumentException("WeatherSummaryMessageMaker는 WeatherSummaryAlarmInfo만 처리할 수 있습니다.");
        }

        validateRequiredDTOs(weatherInfo);
        validateAlarmAgreement(weatherInfo);
    }

    private void validateRequiredDTOs(WeatherSummaryAlarmInfo info) {
        if (info.getWeatherSummaryDTO() == null ||
                info.getExtraWeatherSummaryDTO() == null ||
                info.getAlarmMemberDTO() == null) {

            log.warn("유효성 검사 실패 (필수 DTO 누락): AlarmMemberDTO={} WeatherSummaryDTO={} ExtraWeatherSummaryDTO={}",
                    info.getAlarmMemberDTO() != null ? info.getAlarmMemberDTO().getId() : "null",
                    info.getWeatherSummaryDTO() != null ? "존재함" : "null",
                    info.getExtraWeatherSummaryDTO() != null ? "존재함" : "null");

            throw new IllegalArgumentException("필수 DTO 정보가 누락되었습니다.");
        }
    }

    private void validateAlarmAgreement(WeatherSummaryAlarmInfo info) {
        AlarmMemberDTO member = info.getAlarmMemberDTO();
        WeatherSummaryDTO weather = info.getWeatherSummaryDTO();
        ExtraWeatherSummaryDTO extra = info.getExtraWeatherSummaryDTO();

        if (member.getAgreePrecipAlarm() && weather.getRainStatus() == null) {
            log.warn("유효성 검사 실패 (알림 동의 값 누락): 강수 알림 동의했으나 rainStatus 값이 null. 멤버 ID={}", member.getId());
            throw new IllegalStateException("강수 상태 알림이 설정되어 있으나 rainStatus 값이 없습니다.");
        }

        if (member.getAgreeTempAlarm() &&
                (weather.getMaxTemp() == null || weather.getMinTemp() == null)) {
            log.warn("유효성 검사 실패 (알림 동의 값 누락): 온도 알림 동의했으나 최대/최소 온도 값이 null. 멤버 ID={}", member.getId());
            throw new IllegalStateException("온도 알림이 설정되어 있으나 최대/최소 온도 값이 없습니다.");
        }

        if (member.getAgreeDustAlarm() && extra.getPm10() == null) {
            log.warn("유효성 검사 실패 (알림 동의 값 누락): 미세먼지 알림 동의했으나 pm10 값이 null. 멤버 ID={}", member.getId());
            throw new IllegalStateException("미세먼지 알림이 설정되어 있으나 pm10 값이 없습니다.");
        }

        if (member.getAgreeUvAlarm() &&
                (extra.getMaxUvTime() == null || extra.getMaxUvValue() == null)) {
            log.warn("유효성 검사 실패 (알림 동의 값 누락): 자외선 알림 동의했으나 시간 또는 값이 null. 멤버 ID={}", member.getId());
            throw new IllegalStateException("자외선 알림이 설정되어 있으나 시간 또는 값이 누락되었습니다.");
        }
    }
}
