package org.pknu.weather.service.message;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.pknu.weather.dto.AlarmMemberDTO;
import org.pknu.weather.dto.ExtraWeatherSummaryDTO;
import org.pknu.weather.dto.WeatherSummaryDTO;
import org.pknu.weather.service.dto.AlarmInfo;
import org.pknu.weather.service.dto.WeatherSummaryAlarmInfo;
import org.pknu.weather.service.sender.FcmMessage;
import org.pknu.weather.domain.common.AlarmType;

class WeatherSummaryMessageMakerTest {

    private final WeatherSummaryMessageMaker maker = new WeatherSummaryMessageMaker();

    @Test
    void 정상적인_알림_정보가_주어졌을떄_FcmMessage를_생성한다() {
        // given
        AlarmMemberDTO memberDTO = AlarmMemberDTO.builder()
                .id(1L)
                .fcmToken("token123")
                .agreePrecipAlarm(true)
                .agreeTempAlarm(true)
                .agreeUvAlarm(true)
                .agreeDustAlarm(true)
                .build();

        WeatherSummaryDTO weatherDTO = WeatherSummaryDTO.builder()
                .rainStatus("비")
                .maxTemp(28)
                .minTemp(19)
                .build();

        ExtraWeatherSummaryDTO extraDTO = ExtraWeatherSummaryDTO.builder()
                .locationId(10L)
                .pm10(2)
                .maxUvTime("13")
                .maxUvValue(2)
                .build();

        WeatherSummaryAlarmInfo alarmInfo = new WeatherSummaryAlarmInfo(weatherDTO, extraDTO, memberDTO);

        // when
        FcmMessage result = maker.createAlarmMessage(alarmInfo);

        // then
        assertThat(result).isNotNull()
                .satisfies(res -> {
                    assertThat(res.getFcmToken()).isEqualTo("token123");
                    assertThat(res.getAlarmTitle()).isEqualTo(AlarmType.WEATHER_SUMMARY.getDescription());
                    assertThat(res.getAlarmMessage()).isNotNull();
                });
    }

    @Test
    void AlarmInfo가_WeatherSummaryAlarmInfo_타입이_아니면_예외를_던진다() {
        // given
        AlarmInfo wrongInfo = new AlarmInfo() {};

        // then
        assertThatThrownBy(() -> maker.createAlarmMessage(wrongInfo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("WeatherSummaryAlarmInfo만 처리");
    }

    @Test
    void 필수_DTO가_누락되면_예외를_던진다() {
        // given
        WeatherSummaryAlarmInfo alarmInfo = WeatherSummaryAlarmInfo.builder()
                .alarmMemberDTO(null)
                .weatherSummaryDTO(null)
                .extraWeatherSummaryDTO(null)
                .build();

        // then
        assertThatThrownBy(() -> maker.createAlarmMessage(alarmInfo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("필수 DTO 정보가 누락되었습니다");
    }

    @Test
    void 동의한_항목의_값이_없으면_예외를_던진다_강수() {
        AlarmMemberDTO memberDTO = AlarmMemberDTO.builder()
                .id(2L)
                .fcmToken("token")
                .agreePrecipAlarm(true)
                .agreeTempAlarm(false)
                .build();

        WeatherSummaryDTO weatherDTO = WeatherSummaryDTO.builder()
                .rainStatus(null)
                .build();

        ExtraWeatherSummaryDTO extraDTO = ExtraWeatherSummaryDTO.builder().build();

        WeatherSummaryAlarmInfo alarmInfo = WeatherSummaryAlarmInfo.builder()
                .alarmMemberDTO(memberDTO)
                .weatherSummaryDTO(weatherDTO)
                .extraWeatherSummaryDTO(extraDTO)
                .build();

        assertThatThrownBy(() -> maker.createAlarmMessage(alarmInfo))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("rainStatus");
    }

    @Test
    void 동의한_항목의_값이_없으면_예외를_던진다_기온() {
        AlarmMemberDTO memberDTO = AlarmMemberDTO.builder()
                .id(3L)
                .fcmToken("token")
                .agreeTempAlarm(true)
                .agreePrecipAlarm(false)
                .build();

        WeatherSummaryDTO weatherDTO = WeatherSummaryDTO.builder()
                .maxTemp(null)
                .minTemp(null)
                .build();

        ExtraWeatherSummaryDTO extraDTO = ExtraWeatherSummaryDTO.builder().build();

        WeatherSummaryAlarmInfo alarmInfo = WeatherSummaryAlarmInfo.builder()
                .alarmMemberDTO(memberDTO)
                .weatherSummaryDTO(weatherDTO)
                .extraWeatherSummaryDTO(extraDTO)
                .build();

        assertThatThrownBy(() -> maker.createAlarmMessage(alarmInfo))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("최대/최소 온도");
    }
}