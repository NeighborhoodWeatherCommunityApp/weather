package org.pknu.weather.location.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.location.feignClient.SgisClient;
import org.pknu.weather.location.feignClient.dto.SgisAccessTokenResponseDTO;
import org.pknu.weather.location.feignClient.dto.SgisLocationResponseDTO;
import org.pknu.weather.location.feignClient.dto.SgisLocationWithCoorResponseDTO;
import org.pknu.weather.location.dto.LocationDTO;
import org.pknu.weather.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SgisLocationUtils {

    private final SgisClient sgisClient;
    private String accessToken;
    private LocalDateTime expTime;

    @Value("${spring.sigs.key}")
    String consumerKey;

    @Value("${spring.sigs.secret}")
    String consumerSecret;


    public void getAddressInfo( LocationDTO locationDTO, double x, double y ){

        log.info("SgisLocationUtils - getAddressInfo with coordinate method start .....................");

        checkAccessToken();

        getAddressName(x,y,locationDTO);
        getAddressCoor(locationDTO.getFullAddress(), locationDTO);

    }

    public void getAddressInfo( LocationDTO locationDTO ){

        log.info("SgisLocationUtils - getAddressInfo method start .....................");

        checkAccessToken();

        getAddressCoor(locationDTO.getFullAddress(), locationDTO);

    }

    public void getAddressName( double x, double y, LocationDTO locationDTO){

        log.info("SgisLocationUtils - getAddressName method start .....................");

        SgisLocationResponseDTO location = sgisClient.convertToLocationName(accessToken, x, y, 20);

        SgisLocationResponseDTO.Result addressInfo = location.getResult().get(0);

        locationDTO.setProvince(addressInfo.getSido_nm());
        locationDTO.setCity(addressInfo.getSgg_nm());
        locationDTO.setStreet(addressInfo.getEmdong_nm());

    }

    public void getAddressCoor(String address, LocationDTO locationDTO) {

        log.info("SgisLocationUtils - getAddressCoor method start .....................");

        SgisLocationWithCoorResponseDTO locationCoor = sgisClient.getLocationCoor(accessToken, address);
        SgisLocationWithCoorResponseDTO.ResultData coor = locationCoor.getResult().getResultdata().get(0);

        locationDTO.setLongitude(coor.getX());
        locationDTO.setLatitude(coor.getY());
    }

    public void checkAccessToken(){

        log.info("SgisLocationUtils - checkAccessToken method start .....................");

        if (accessToken == null || accessToken.isEmpty() || expTime.isBefore(LocalDateTime.now().minusMinutes(10))) {
            getSgisAccessToken();
        }
    }

    public synchronized void getSgisAccessToken(){

        if (accessToken == null || accessToken.isEmpty() || expTime.isBefore(LocalDateTime.now().minusMinutes(10))) {

            log.info("SgisLocationUtils - getSgisAccessToken method start .....................");

            SgisAccessTokenResponseDTO sgisAccessToken = sgisClient.getSgisAccessToken(consumerKey,consumerSecret);

            log.debug(sgisAccessToken.getErrMsg());

            switch (sgisAccessToken.getErrCd()){
                case (401):
                    throw new GeneralException(ErrorStatus._SGIS_BAD_AUTHENTICATION_PARAMETER);
                case (100):
                    throw new GeneralException(ErrorStatus._SGIS_NOT_FOUND_RESULT);
                case(0):
                    this.accessToken = sgisAccessToken.getResult().getAccessToken();
                    this.expTime = convertMillis(sgisAccessToken.getResult().getAccessTimeout());
                    break;
                default:
                    throw new RuntimeException();
            }

        }

    }

    public LocalDateTime convertMillis(long timeMillis){
        Instant instant = Instant.ofEpochMilli(timeMillis);
        ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());
        return dateTime.toLocalDateTime();
    }
}
