package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.dto.converter.WeatherConverter;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 메인페이지에서 사용되는 API를 위한 서비스 즉, 화면에 맞춰진 로직을 관리한다.
 * 해당 서비스는 서비스를 의존할 수 있다. 단 핵심 비즈니스 로직만 의존한다.
 * 서비스를 참조하는 서비스를 한 곳으로 몰아서 서비스간 순환 참조를 방지한다.
 * 순환 참조가 발생하면 잘못된 설계이다!
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainPageService {
    private final MemberRepository memberRepository;
    private final WeatherService weatherService;
    private final PostService postService;

    public WeatherResponse.MainPageWeatherData getWeatherInfo(Long memberId) {
        Member member = memberRepository.safeFindById(memberId);
        List<Weather> weatherList = weatherService.getWeathers(member);
        return WeatherConverter.toMainPageWeatherData(weatherList, member);
    }


}
