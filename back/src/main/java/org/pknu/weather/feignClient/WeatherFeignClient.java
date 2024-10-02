package org.pknu.weather.feignClient;

import org.pknu.weather.common.WeatherParamsFactory;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherApiResponse;
import org.pknu.weather.dto.WeatherApiResponse.Response.Body.Items.Item;
import org.pknu.weather.feignClient.dto.WeatherParams;
import org.pknu.weather.feignClient.dto.PointDTO;
import org.pknu.weather.feignClient.utils.WeatherApiUtils;
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
