package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.service.MainPageService;
import org.pknu.weather.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

/**
 * 메인 화면에 사용되는 API를 관리하는 컨트롤러. 화면용입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainPageControllerV1 {
    private final MainPageService mainPageService;
    private final WeatherService weatherService;

    @GetMapping("/weather")
    public ApiResponse<WeatherResponse.MainPageWeatherData> getMainPageResource(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) Long locationId) {

        String email = TokenConverter.getEmailByToken(authorization);
        WeatherResponse.MainPageWeatherData weatherInfo = mainPageService.getWeatherInfo(email, locationId);

        return ApiResponse.onSuccess(weatherInfo);
    }

    @GetMapping("/posts/popular")
    public ApiResponse<List<PostResponse.Post>> getPopularPostList(
            @RequestHeader("Authorization") String authorization) {

        String email = TokenConverter.getEmailByToken(authorization);
        List<PostResponse.Post> popularPosts = mainPageService.getPopularPosts(email);
        return ApiResponse.onSuccess(popularPosts);
    }

    @GetMapping(value = "/extraWeatherInfo")
    public ApiResponse<WeatherResponse.ExtraWeatherInfo> getExtraWeatherInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) Long locationId) {

        String email = getEmailByToken(authorization);
        WeatherResponse.ExtraWeatherInfo extraWeatherInfo = weatherService.extraWeatherInfo(email, locationId);

        return ApiResponse.onSuccess(extraWeatherInfo);
    }

    @GetMapping("/weather/simple/tags")
    public ApiResponse<List<TagDto.SimpleTag>> getMostSelectedTags(
            @RequestHeader("Authorization") String authorization) {
        String email = TokenConverter.getEmailByToken(authorization);
        List<TagDto.SimpleTag> mostTags = mainPageService.getMostSelectedTags(email);
        return ApiResponse.onSuccess(mostTags);
    }

    @GetMapping("/weather/simple/rain")
    public ApiResponse<Object> getRainProbability(@RequestHeader("Authorization") String authorization) {
        String email = TokenConverter.getEmailByToken(authorization);
        WeatherResponse.SimpleRainInformation rainProb = mainPageService.getSimpleRainInfo(email);
        return ApiResponse.onSuccess(rainProb);
    }

}
