/*
package org.pknu.weather.common.sgis;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pknu.weather.location.feignClient.SgisClient;
import org.pknu.weather.location.feignClient.dto.SgisAccessTokenResponseDTO;
import org.pknu.weather.location.feignClient.dto.SgisLocationResponseDTO;
import org.pknu.weather.location.feignClient.dto.SgisLocationWithCoorResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SgisFeignTest {

    @Autowired
    SgisClient sgisClient;

    @Value("${spring.sigs.key}")
    String consumerKey;

    @Value("${spring.sigs.secret}")
    String consumerSecret;

    @Test
    public void getSigsAccessToken(){
        SgisAccessTokenResponseDTO result = sgisClient.getSgisAccessToken(consumerKey,consumerSecret);

        log.info("\naccessToken : " + result.getResult().getAccessToken() + "\ntimeout : " + result.getResult().getAccessTimeout());
    }

    @Test
    public void getAddressInfo() {

        SgisAccessTokenResponseDTO tokenResult = sgisClient.getSgisAccessToken(consumerKey,consumerSecret);

        String accessToken = tokenResult.getResult().getAccessToken();

        SgisLocationResponseDTO location = sgisClient.convertToLocationName(accessToken, 127.392925, 36.343492, 20);

        SgisLocationResponseDTO.Result result = location.getResult().get(0);

        log.info(result.getSido_nm() + result.getSgg_nm() + result.getEmdong_nm());

    }

    @Test
    public void getAddressCoor() {

        SgisAccessTokenResponseDTO tokenResult = sgisClient.getSgisAccessToken(consumerKey,consumerSecret);

        String accessToken = tokenResult.getResult().getAccessToken();

        SgisLocationResponseDTO location = sgisClient.convertToLocationName(accessToken, 127.392925, 36.343492, 20);

        SgisLocationResponseDTO.Result result = location.getResult().get(0);

        log.info(result.getSido_nm() + result.getSgg_nm() + result.getEmdong_nm());

        String address = result.getSido_nm() + result.getSgg_nm() + result.getEmdong_nm();

        SgisLocationWithCoorResponseDTO locationCoor = sgisClient.getLocationCoor(accessToken, address);
        SgisLocationWithCoorResponseDTO.Result resultCoor = locationCoor.getResult();
        SgisLocationWithCoorResponseDTO.ResultData coor = resultCoor.getResultdata().get(0);

        log.info(String.valueOf(coor.getX()));
        log.info(String.valueOf(coor.getY()));

    }

}

 */



