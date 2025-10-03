package org.pknu.weather.weather.feignclient.weatherapi;

import org.pknu.weather.weather.feignclient.error.CommonErrorDecoder;
import org.pknu.weather.weather.feignclient.weatherapi.dto.KmsApiHubParamDTO;
import org.pknu.weather.weather.feignclient.weatherapi.dto.KmsApiHubResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "kms",
        url = "https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0",
        configuration = CommonErrorDecoder.class)
public interface KmaApiHubFeignClient {

    @RequestMapping(
            headers = "Accept=application/json",
            method = RequestMethod.GET,
            value = "/getVilageFcst",
            produces = "application/json")
    KmsApiHubResponseDTO getVillageShortTermForecast(@SpringQueryMap KmsApiHubParamDTO param);
}
