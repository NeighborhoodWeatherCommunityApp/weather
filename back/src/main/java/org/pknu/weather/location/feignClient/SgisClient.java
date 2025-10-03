package org.pknu.weather.location.feignClient;


import org.pknu.weather.location.feignClient.dto.SgisAccessTokenResponseDTO;
import org.pknu.weather.location.feignClient.dto.SgisLocationResponseDTO;
import org.pknu.weather.location.feignClient.dto.SgisLocationWithCoorResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "location",url = "https://sgisapi.kostat.go.kr/OpenAPI3")
public interface SgisClient {

    @GetMapping("/auth/authentication.json")
    SgisAccessTokenResponseDTO getSgisAccessToken(@RequestParam("consumer_key") String consumerKey,
                                                  @RequestParam("consumer_secret") String consumerSecret);
    @GetMapping("/addr/rgeocodewgs84.json")
    SgisLocationResponseDTO convertToLocationName(@RequestParam("accessToken") String accessToken,
                                                  @RequestParam("x_coor") double xCoor,
                                                  @RequestParam("y_coor") double yCoor,
                                                  @RequestParam("addr_type") int addrType);
    @GetMapping("/addr/geocodewgs84.json")
    SgisLocationWithCoorResponseDTO getLocationCoor(@RequestParam("accessToken") String accessToken,
                                                    @RequestParam("address") String address);

}
