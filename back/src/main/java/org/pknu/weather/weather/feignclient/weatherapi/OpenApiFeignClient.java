package org.pknu.weather.weather.feignclient.weatherapi;

import org.pknu.weather.weather.feignclient.error.CommonErrorDecoder;
import org.pknu.weather.weather.feignclient.weatherapi.dto.OpenApiParamDTO;
import org.pknu.weather.weather.feignclient.weatherapi.dto.OpenApiResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "open-api",
        url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0",
        configuration = CommonErrorDecoder.class)
public interface OpenApiFeignClient {

    @RequestMapping(
            headers = "Accept=application/json",
            method = RequestMethod.GET,
            value = "/getVilageFcst",
            produces = "application/json")
    OpenApiResponseDTO getVillageShortTermForecast(@SpringQueryMap OpenApiParamDTO param);
}
