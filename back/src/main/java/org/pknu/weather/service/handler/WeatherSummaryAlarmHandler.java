package org.pknu.weather.service.handler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.weather.ExtraWeather;
import org.pknu.weather.weather.converter.ExtraWeatherConverter;
import org.pknu.weather.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.pknu.weather.service.message.AlarmMessageMaker;
import org.pknu.weather.service.sender.NotificationMessage;
import org.pknu.weather.service.sender.NotificationSender;
import org.pknu.weather.dto.AlarmMemberDTO;
import org.pknu.weather.weather.dto.ExtraWeatherSummaryDTO;
import org.pknu.weather.service.dto.WeatherSummaryAlarmInfo;
import org.pknu.weather.weather.dto.WeatherSummaryDTO;
import org.pknu.weather.service.supports.AlarmTimeUtil;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.weather.service.WeatherRefresherService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherSummaryAlarmHandler implements NoArgsAlarmHandler {

    private final MemberRepository memberRepository;
    private final WeatherRepository weatherRepository;
    private final ExtraWeatherRepository extraWeatherRepository;
    private final AlarmMessageMaker weatherSummaryMessageMaker;
    private final NotificationSender sender;
    private final WeatherRefresherService weatherRefresherService;

    @Override
    public AlarmType getAlarmType() {
        return AlarmType.WEATHER_SUMMARY;
    }

    @Override
    public void handleRequest() {
        List<AlarmMemberDTO> alarmMembers = memberRepository.findMembersAndAlarmsByAlarmTime(AlarmTimeUtil.getCurrentAlarmTime());
        List<AlarmMemberDTO> failedMembers = dispatchAlarmsForMembers(alarmMembers);

        if (!failedMembers.isEmpty()) {
            retryFailedMembers(failedMembers);
        }
    }

    private List<AlarmMemberDTO> dispatchAlarmsForMembers(List<AlarmMemberDTO> members) {
        Set<Long> alarmLocations = extractLocationIds(members);
        List<WeatherSummaryDTO> weatherSummaries = weatherRepository.findWeatherSummary(alarmLocations);
        List<ExtraWeather> extraWeathers = extraWeatherRepository.findExtraWeatherByLocations(alarmLocations, LocalDateTime.now().minusHours(4)); //자외선 수치는 3시간 단위로 저장되어 있음

        List<ExtraWeatherSummaryDTO> extraWeatherSummaries = extraWeathers.stream()
                .map(ExtraWeatherConverter::toExtraWeatherSummaryDTO)
                .collect(Collectors.toList());

        Map<Long, List<AlarmMemberDTO>> groupedMembersByLocation = groupMembersByLocation(members);
        Map<Long, WeatherSummaryDTO> weatherMap = toMap(weatherSummaries, WeatherSummaryDTO::getLocationId);
        Map<Long, ExtraWeatherSummaryDTO> extraMap = toMap(extraWeatherSummaries, ExtraWeatherSummaryDTO::getLocationId);

        return groupedMembersByLocation.entrySet().stream()
                .flatMap(entry -> sendAlarmsForLocation(entry.getKey(), entry.getValue(), weatherMap, extraMap).stream())
                .collect(Collectors.toList());
    }

    private List<AlarmMemberDTO> sendAlarmsForLocation(Long locationId, List<AlarmMemberDTO> members,
                                                       Map<Long, WeatherSummaryDTO> weatherMap,
                                                       Map<Long, ExtraWeatherSummaryDTO> extraMap) {

        WeatherSummaryDTO weather = weatherMap.get(locationId);
        ExtraWeatherSummaryDTO extra = extraMap.get(locationId);

        return members.stream()
                .filter(member -> !sendSingleAlarm(locationId, member, weather, extra))
                .collect(Collectors.toList());
    }

    boolean sendSingleAlarm(Long locationId, AlarmMemberDTO member, WeatherSummaryDTO weather,
                            ExtraWeatherSummaryDTO extra) {
        try {
            NotificationMessage message = weatherSummaryMessageMaker.createAlarmMessage(
                    new WeatherSummaryAlarmInfo(weather, extra, member)
            );

            sender.send(message);
            return true;

        } catch (RuntimeException e) {
            log.warn("[알림 실패] locationId={}, memberId={}, reason={}", locationId, member.getId(), e.getMessage(), e);
            return false;
        }
    }

    private void retryFailedMembers(List<AlarmMemberDTO> failedMembers) {
        Set<Long> locationIds = extractLocationIds(failedMembers);

        weatherRefresherService.refresh(locationIds);

        List<AlarmMemberDTO> retryFailures = dispatchAlarmsForMembers(failedMembers);

        if (!retryFailures.isEmpty()) {
            log.error("[알람 재시도 실패] {}명의 알림이 최종 실패했습니다.", retryFailures.size());
        }
    }

    private static Set<Long> extractLocationIds(List<AlarmMemberDTO> members) {
        return members.stream()
                .map(AlarmMemberDTO::getLocationId)
                .collect(Collectors.toSet());
    }

    private <T> Map<Long, T> toMap(List<T> list, Function<T, Long> keyMapper) {
        if (list == null) {
            return Collections.emptyMap();
        }

        return list.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
    }

    private Map<Long, List<AlarmMemberDTO>> groupMembersByLocation(List<AlarmMemberDTO> members) {
        return members.stream().collect(Collectors.groupingBy(AlarmMemberDTO::getLocationId));
    }
}

