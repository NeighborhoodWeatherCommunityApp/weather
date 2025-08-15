package org.pknu.weather.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.weather.service.WeatherRefresherService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class WeatherControllerForAdmin {
    private final WeatherRefresherService weatherRefresherService;

    @PostMapping("/redis/weather")
    public ApiResponse<Object> refreshWeatherRedisData(@RequestHeader("Authorization") String authorization) {
        weatherRefresherService.updateWeatherCachedDataScheduled(100);
        return ApiResponse.onSuccess();
    }
}
