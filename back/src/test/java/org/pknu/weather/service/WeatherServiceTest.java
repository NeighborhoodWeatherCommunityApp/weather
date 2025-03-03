//package org.pknu.weather.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.pknu.weather.common.utils.GeometryUtils;
//import org.pknu.weather.domain.Location;
//import org.pknu.weather.domain.Member;
//import org.pknu.weather.domain.Weather;
//import org.pknu.weather.domain.common.Sensitivity;
//import org.pknu.weather.feignClient.WeatherFeignClient;
//import org.pknu.weather.repository.LocationRepository;
//import org.pknu.weather.repository.MemberRepository;
//import org.pknu.weather.repository.WeatherRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class WeatherServiceTest {
//    @Autowired
//    WeatherService weatherService;
//
//    @Autowired
//    WeatherRepository weatherRepository;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    LocationRepository locationRepository;
//
//    @Autowired
//    WeatherFeignClient weatherFeignClient;
//
//    private final double LATITUDE = 35.1845361111111;
//    private final double LONGITUDE = 128.989688888888;
//
//    @BeforeEach
//    void init() {
//        Location location = Location.builder()
//                .point(GeometryUtils.getPoint(LATITUDE, LONGITUDE))
//                .city("city")
//                .province("province")
//                .street("street")
//                .latitude(LATITUDE)
//                .longitude(LONGITUDE)
//                .build();
//
//        Member member = Member.builder()
//                .location(location)
//                .email("email@naver.com")
//                .nickname("nickname")
//                .sensitivity(Sensitivity.NONE)
//                .build();
//
//        locationRepository.save(location);
//        memberRepository.save(member);
//    }
//
//    @Test
//    @DisplayName("단기 예보 저장 통합 테스트")
//    @Transactional
//    void shortTermForecastSaveTest() {
//        // given
//        Member member = memberRepository.findAll().get(0);
//        Location location = member.getLocation();
//
//        // when
//        // TODO: WeatherFeignClient.test 에서 API가 호출되는지 테스트를 하기 때문에 Mock으로 변경
//        List<Weather> weathers = weatherService.saveWeathers(location);
//
//        // then
//        assertThat(weathers.size()).isGreaterThanOrEqualTo(20);
//    }
//}
