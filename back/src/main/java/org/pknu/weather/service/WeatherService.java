package org.pknu.weather.service;

import com.sun.tools.javac.Main;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.common.*;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.converter.WeatherResponse;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.pknu.weather.dto.WeatherApiResponse.Response.Body.Items.Item;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WeatherService {
    private final WeatherFeignClient weatherFeignClient;
    private final WeatherRepository weatherRepository;
    private final MemberRepository memberRepository;

    /**
     * 위도와 경도에 해당하는 지역(읍면동)의 24시간치 날씨 단기 예보 정보를 저장합니다.
     * @param memberId
     * @param lon 경도
     * @param lat 위도
     * @return 위도와 경도에 해당하는 Location의 Weather list를 반환
     */
    public List<Weather> saveWeather(Long memberId, Float lon, Float lat) {
        log.debug("%logger{0}, %M, memberId: {}, lon: {}, lat: {}", memberId, lon, lat);

        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
        ArrayList<Weather> weatherList = new ArrayList<>(weatherFeignClient.preprocess(lon, lat).values());

        weatherList.forEach(w -> w.addLocation(location));
        return weatherRepository.saveAll(weatherList);
    }
}
