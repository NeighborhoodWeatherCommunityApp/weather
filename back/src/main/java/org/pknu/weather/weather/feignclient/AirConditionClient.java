package org.pknu.weather.weather.feignclient;

import org.pknu.weather.weather.feignclient.dto.AirConditionResponseDTO;
import org.pknu.weather.weather.feignclient.dto.AirObservatoryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "AirCondition", url = "https://apis.data.go.kr/B552584")
public interface AirConditionClient {

    @GetMapping("/MsrstnInfoInqireSvc/getNearbyMsrstnList")
    AirObservatoryResponseDTO getObservatoryInfo(@RequestParam("serviceKey") String serviceKey,
                                                 @RequestParam("returnType") String returnType,
                                                 @RequestParam("tmX") Double tmX,
                                                 @RequestParam("tmY") Double tmY,
                                                 @RequestParam("ver") Double ver);

    @GetMapping("/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty")
    AirConditionResponseDTO getAirConditionInfo(@RequestParam("serviceKey") String serviceKey,
                                                @RequestParam("returnType") String returnType,
                                                @RequestParam("stationName") String stationName,
                                                @RequestParam("dataTerm") String dataTerm,
                                                @RequestParam("ver") Double ver);

}
