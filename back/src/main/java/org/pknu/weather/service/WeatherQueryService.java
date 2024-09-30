package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.dto.WeatherQueryResult;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.dto.converter.WeatherResponseConverter;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WeatherQueryService {
    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;
    private final WeatherRepository weatherRepository;

    public WeatherResponse.SimpleRainInformation getSimpleRainInfo(Long memberId) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
        WeatherQueryResult.SimpleRainInfo simpleRainInfo = weatherRepository.getSimpleRainInfo(location);
        return WeatherResponseConverter.toSimpleRainInformation(simpleRainInfo);
    }

    /**
     * 해당 지역의 날씨가 갱신되었는지 확인하는 메서드
     * @param location
     * @return true = 갱신되었음(3시간 안지남), false = 갱신되지 않았음(3시간 지남)
     */
    public boolean weatherHasBeenUpdated(Location location) {
        return weatherRepository.weatherHasBeenUpdated(location);
    }

    /**
     * 해당 지역의 날씨 데이터가 존재하는지 확인하는 메서드
     * @param location
     * @return true = 존재함, false = 존재 하지 않음
     */
    public boolean weatherHasBeenCreated(Location location) {
        return weatherRepository.weatherHasBeenCreated(location);
    }

}
