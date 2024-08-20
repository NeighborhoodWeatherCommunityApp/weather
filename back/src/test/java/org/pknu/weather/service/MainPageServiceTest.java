package org.pknu.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.dto.converter.WeatherResponseConverter;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Slf4j
class MainPageServiceTest {
    @Autowired
    MainPageService mainPageService;

    @Autowired
    WeatherService weatherService;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WeatherRepository weatherRepository;

    private final double LATITUDE = 35.1845361111111;
    private final double LONGITUDE = 128.989688888888;
    private final String VILLAGE_NAME = "사상구 모라동";

    @BeforeEach
    void init() {
        Location location = Location.builder()
                .city("사상구")
                .province("province")
                .street("모라동")
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
        member = memberRepository.save(member);
        weatherService.saveWeathers(member.getId(), (float)LONGITUDE, (float)LATITUDE);
    }

    @Test
    @DisplayName("메인 페이지의 날씨 예보와 관련된 정보를 불러오는 API 통합 테스트")
    @Transactional
    void mainPageWeatherApiTest(){
        // given
        Member member = memberRepository.findAll().get(0);
        List<Weather> weatherList = weatherService.getWeathers(member);

        // when
        WeatherResponseConverter.MainPageWeatherData weatherInfo = mainPageService.getWeatherInfo(member.getId());

        for (Weather w : weatherList) {
            log.info(w.getPresentationTime() + " " + w.getTemperature());
        }

        for (WeatherResponseConverter.WeatherPerHour wph : weatherInfo.getWeatherPerHourList()) {
            log.info(wph.getHour() + " " + wph.getSkyType() + " " + wph.getRainAdverb() + " " + wph.getRainText() + " " + wph.getRain()
                    + " " + wph.getTmpAdverb() + " " + wph.getTmpText() + " " + wph.getTmp());
        }

        // then
        assertThat(weatherInfo.getCurrentTmp()).isEqualTo(weatherList.get(0).getTemperature());
        assertThat(weatherInfo.getWeatherPerHourList().size()).isGreaterThanOrEqualTo(21);  // 3시간 간격으로 예보를 발표하기 때문에 22 ~ 24개의 예보
        assertThat(weatherInfo.getCurrentSkyType()).isEqualTo(weatherList.get(0).getSkyType());
    }
}