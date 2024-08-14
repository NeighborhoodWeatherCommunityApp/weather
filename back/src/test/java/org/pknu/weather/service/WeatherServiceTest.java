package org.pknu.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.WeatherFeignClient;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
class WeatherServiceTest {
    @Autowired
    WeatherService weatherService;

    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    WeatherFeignClient weatherFeignClient;

    private final double LATITUDE = 35.1845361111111;
    private final double LONGITUDE = 128.989688888888;

    @BeforeEach
    void init() {
        Location location = Location.builder()
                .city("city")
                .province("province")
                .street("street")
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .build();

        Member member = Member.builder()
                .location(location)
                .email("email@naver.com")
                .nickname("nickname")
                .sensitivity(Sensitivity.NONE)
                .build();

        locationRepository.save(location);
        memberRepository.save(member);
    }

    @Test
    @DisplayName("단기 예보 저장 통합 테스트")
    @Transactional
    void shortTermForecastSaveTest() {
        // given
        Member member = memberRepository.safeFindById(1L);

        // when
        List<Weather> weathers = weatherService.saveWeather(member.getId(), (float)LONGITUDE, (float)LATITUDE);

        // then
        assertThat(weathers.size()).isEqualTo(24);
    }
}