package org.pknu.weather.service.handler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.Alarm;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.dto.AlarmMemberDTO;
import org.pknu.weather.dto.ExtraWeatherSummaryDTO;
import org.pknu.weather.dto.WeatherSummaryDTO;
import org.pknu.weather.dto.converter.ExtraWeatherConverter;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.repository.AlarmRepository;
import org.pknu.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.pknu.weather.service.dto.WeatherSummaryAlarmInfo;
import org.pknu.weather.service.message.AlarmMessageMaker;
import org.pknu.weather.service.sender.NotificationMessage;
import org.pknu.weather.service.sender.NotificationSender;
import org.pknu.weather.service.supports.WeatherRefresherService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestWeatherSummaryAlarmHandler implements ArgsAlarmHandler<Map<String, String>>{

    private final WeatherRepository weatherRepository;
    private final MemberRepository memberRepository;
    private final ExtraWeatherRepository extraWeatherRepository;
    private final AlarmMessageMaker weatherSummaryMessageMaker;
    private final NotificationSender sender;
    private final WeatherRefresherService weatherRefresherService;
    private final AlarmRepository alarmRepository;

    @Override
    public AlarmType getAlarmType() {
        return AlarmType.TEST_WEATHER_SUMMARY;
    }


    @Override
    public void handleRequest(Map<String, String> payload) {
        String fcmToken = payload.get("fcmToken");
        Member member= memberRepository.findMemberByEmail(payload.get("email"))
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));


        Alarm foundAlarm = alarmRepository.findByFcmTokenAndMember(fcmToken,member).orElseThrow(() -> new GeneralException(
                ErrorStatus._FCMTOKEN_NOT_FOUND));
        Member foundMember = foundAlarm.getMember();
        Set<Long> alarmLocations = Collections.singleton(foundMember.getLocation().getId());

        weatherRefresherService.refresh(alarmLocations);

        AlarmMemberDTO alarmMemberDTO = getAlarmMemberDTO(fcmToken, foundMember, foundAlarm);

        WeatherSummaryDTO weatherSummaries = weatherRepository.findWeatherSummary(alarmLocations).get(0);
        List<ExtraWeather> extraWeathers = extraWeatherRepository.findExtraWeatherByLocations(alarmLocations, LocalDateTime.now().minusHours(3));
        ExtraWeatherSummaryDTO extraWeatherSummaries = ExtraWeatherConverter.toExtraWeatherSummaryDTO(extraWeathers.get(0));

        sendSingleAlarm(alarmMemberDTO, weatherSummaries, extraWeatherSummaries);
    }

    private static AlarmMemberDTO getAlarmMemberDTO(String fcmToken, Member foundMember, Alarm foundAlarm) {
        return AlarmMemberDTO.builder()
                .id(foundMember.getId())
                .fcmToken(fcmToken)
                .locationId(foundMember.getLocation().getId())
                .agreeDustAlarm(foundAlarm.getAgreeDustAlarm())
                .agreeUvAlarm(foundAlarm.getAgreeUvAlarm())
                .agreePrecipAlarm(foundAlarm.getAgreePrecipAlarm())
                .agreeTempAlarm(foundAlarm.getAgreeTempAlarm())
                .agreeLiveRainAlarm(foundAlarm.getAgreeLiveRainAlarm())
                .build();
    }


    void sendSingleAlarm(AlarmMemberDTO member, WeatherSummaryDTO weather,
                            ExtraWeatherSummaryDTO extra) {

        NotificationMessage message = weatherSummaryMessageMaker.createAlarmMessage(
                new WeatherSummaryAlarmInfo(weather, extra, member));

        sender.send(message);

    }

}

