package org.pknu.weather.feignClient;

import org.pknu.weather.weather.dto.WeatherApiResponseDTO;
import org.pknu.weather.feignClient.dto.WeatherParams;
import org.pknu.weather.feignClient.error.CommonErrorDecoder;
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
    WeatherApiResponseDTO getVillageShortTermForecast(@SpringQueryMap WeatherParams weatherRequest);
}
