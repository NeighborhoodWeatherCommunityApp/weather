package org.pknu.weather.service.handler;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.dto.AlarmMemberDTO;
import org.pknu.weather.service.supports.AlarmTimeUtil;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.weather.service.WeatherRefresherService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherUpdateHandler implements NoArgsAlarmHandler {

    private final MemberRepository memberRepository;
    private final WeatherRefresherService weatherRefresherService;


    @Override
    public AlarmType getAlarmType() {
        return AlarmType.WEATHER_UPDATE;
    }

    @Override
    public void handleRequest() {

        List<AlarmMemberDTO> alarmMember = memberRepository.findMembersAndAlarmsByAlarmTime(AlarmTimeUtil.getCurrentAlarmTime());

        Set<Long> searchedLocation = alarmMember.stream()
                .map(AlarmMemberDTO::getLocationId)
                .collect(Collectors.toSet());

        weatherRefresherService.refresh(searchedLocation);
    }


}
