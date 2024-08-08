package org.pknu.weather.common;

import org.pknu.weather.dto.WeatherApiResponse;
import org.pknu.weather.dto.WeatherParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "weather", url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
public interface WeatherFeignClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/getVilageFcst",
            produces = "application/json")
    WeatherApiResponse getVillageShortTermForecast(@SpringQueryMap WeatherParams weatherRequest);
}
