package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.common.WeatherFeignClient;
import org.springframework.stereotype.Service;

/**
 * 메인페이지에서 사용되는 API를 위한 서비스 즉, 화면에 맞춰진 로직을 관리한다.
 * 해당 서비스는 서비스를 의존할 수 있다. 단 핵심 비즈니스 로직만 의존한다.
 * 서비스를 참조하는 서비스를 한 곳으로 몰아서 서비스간 순환 참조를 방지한다.
 * 순환 참조가 발생하면 잘못된 설계이다!
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MainPageApiService {
    private final WeatherFeignClient weatherFeignClient;
    private final WeatherService weatherService;

    /**
     * 사용자가 메인페이지에 접근했을 때 동작한다. 로직은 아래와 같다.
     * 1. 사용자의 위도 경도 및 기타 정보를 받아와 nx, ny로 치환한다 -> c~~utils 처리 o
     * 2. 공공데이터 API를 호출하고, 응답값을 받아온다.  o
     * 3. 사용자의 nx, ny를 member entity에 저장한다.
     * 4. location entity에 지역 정보를 저장한다.
     * 5. weather entity에 날씨 예보 정보를 저장한다..
     */
}
