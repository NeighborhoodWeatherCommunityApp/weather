package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.converter.WeatherResponseConverter;
import org.pknu.weather.service.MainPageService;
import org.springframework.web.bind.annotation.*;

/**
 * 메인 화면에 사용되는 API를 관리하는 컨트롤러. 화면용입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainPageControllerV1 {
    private final MainPageService mainPageService;

    @GetMapping("/weather")
    public ApiResponse<WeatherResponseConverter.MainPageWeatherData> getMainPageResource(@RequestParam Long memberId) {
        WeatherResponseConverter.MainPageWeatherData weatherInfo = mainPageService.getWeatherInfo(memberId);
        return ApiResponse.onSuccess(weatherInfo);
    }

}
