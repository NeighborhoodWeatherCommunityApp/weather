package org.pknu.weather.weather.feignClient;

import org.pknu.weather.weather.dto.WeatherApiResponse;
import org.pknu.weather.weather.feignClient.dto.WeatherParams;
import org.pknu.weather.common.feignClient.error.CommonErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "weather",
        url = "${api.weather.forecast-url}",
        configuration = CommonErrorDecoder.class)
public interface WeatherFeignClient {

    @RequestMapping(
            headers = "Accept=application/json",
            method = RequestMethod.GET,
            value = "${api.weather.forecast-value}",
            produces = "application/json")
    WeatherApiResponse getVillageShortTermForecast(@SpringQueryMap WeatherParams weatherRequest);
}
