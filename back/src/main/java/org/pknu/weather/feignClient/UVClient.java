package org.pknu.weather.feignClient;

import feign.Response;
import org.pknu.weather.feignClient.dto.UVResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "UV", url = "http://apis.data.go.kr/1360000/LivingWthrIdxServiceV4")
public interface UVClient {


    @GetMapping("/getUVIdxV4")
    UVResponseDTO getUVInfo(@RequestParam("ServiceKey") String ServiceKey,
                       @RequestParam("areaNo") Long areaNo,
                       @RequestParam("time") String time,
                       @RequestParam("dataType") String dataType);

}
