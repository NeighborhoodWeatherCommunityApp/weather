package org.pknu.weather.feignClient.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.feignClient.AirConditionClient;
import org.pknu.weather.feignClient.UVClient;
import org.pknu.weather.feignClient.dto.AirConditionResponseDTO;
import org.pknu.weather.feignClient.dto.AirObservatoryResponseDTO;
import org.pknu.weather.feignClient.dto.UVResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.pknu.weather.common.converter.CoordinateConverter.transformWGS84ToUTMK;
import static org.pknu.weather.common.formatter.DateTimeFormatter.getFormattedDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExtraWeatherApiUtils {

    @Value("${spring.weather.key}")
    private String weatherKey;
    private static final String DATATYPE = "JSON";
    private static final String DATATERM = "DAILY";


    private final UVClient uvClient;
    private final AirConditionClient airConditionClient;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public WeatherResponse.ExtraWeatherInfo getExtraWeatherInfo(LocationDTO locationDTO){


        UVResponseDTO.Item uvResult = getUV(locationDTO);

        AirConditionResponseDTO result = getAirConditionInfo(locationDTO);
        AirConditionResponseDTO.Item airConditionInfo = result.getResponse().getBody().getItems().get(0);


        return WeatherResponse.ExtraWeatherInfo.builder()
                .baseTime(convertToLocalDateTime(uvResult.getDate()))
                .o3Grade(airConditionInfo.getO3Grade())
                .pm10Grade(airConditionInfo.getPm10Grade())
                .pm25Grade(airConditionInfo.getPm25Grade())
                .uvGrade(uvResult.getH0())
                .build();
    }

    public WeatherResponse.ExtraWeatherInfo getExtraWeatherInfo(LocationDTO locationDTO, LocalDateTime baseTime){

        UVResponseDTO.Item uvResult = getUV(locationDTO);

        if (baseTime.toLocalDate().isBefore(LocalDate.now())){
            AirConditionResponseDTO result = getAirConditionInfo(locationDTO);
            AirConditionResponseDTO.Item airConditionInfo = result.getResponse().getBody().getItems().get(0);

            return WeatherResponse.ExtraWeatherInfo.builder()
                    .baseTime(convertToLocalDateTime(uvResult.getDate()))
                    .o3Grade(airConditionInfo.getO3Grade())
                    .pm10Grade(airConditionInfo.getPm10Grade())
                    .pm25Grade(airConditionInfo.getPm25Grade())
                    .uvGrade(uvResult.getH0())
                    .build();
        }

        return WeatherResponse.ExtraWeatherInfo.builder()
                .baseTime(convertToLocalDateTime(uvResult.getDate()))
                .uvGrade(uvResult.getH0())
                .build();
    }

    private UVResponseDTO.Item getUV(LocationDTO locationDTO){

        Long locationCode = getLocation(locationDTO);

        if (locationCode  == null)
            throw new GeneralException(ErrorStatus._LOCATION_NOT_FOUND);

        String date = getFormattedDate() + LocalTime.now().getHour();

        UVResponseDTO result = uvClient.getUVInfo(weatherKey, locationCode, date, DATATYPE);

        return result.getResponse().getBody().getItems().getItem().get(0);
    }

    private Long getLocation(LocationDTO locationDTO){

        Long locationCode = getLocationCode(locationDTO.getProvince(), locationDTO.getCity(), locationDTO.getStreet());

        if (locationCode == null) {
            locationCode = getLocationCode(locationDTO.getProvince(), locationDTO.getCity());
        }

        if (locationCode == null) {
            locationCode = getLocationCode(locationDTO.getProvince());
        }

        return locationCode;
    }

    private Long getLocationCode(String province, String city, String street){

        String sql = "SELECT C1 FROM location_code WHERE C2 = :province AND C3 = :city AND C4 = :street";

        try {

            Map<String, Object> param = Map.of("province", province,"city",city,"street",street);
            return jdbcTemplate.queryForObject(sql, param, Long.class);

        } catch (EmptyResultDataAccessException e) {
            log.warn("locationCode를 찾을 수 없습니다.(with Street)");
        }

        return null;
    }

    private Long getLocationCode(String province, String city){

        String sql = "SELECT C1 FROM location_code WHERE C2 = :province AND C3 = :city AND C4 IS NULL";

        try {

            Map<String, Object> param = Map.of("province",province,"city",city);
            return jdbcTemplate.queryForObject(sql, param, Long.class);

        } catch (EmptyResultDataAccessException e) {
            log.warn("locationCode를 찾을 수 없습니다.(with City)");
        }

        return null;
    }

    private Long getLocationCode(String province){

        String sql = "SELECT C1 FROM location_code WHERE C2 = :province AND C3 IS NULL AND C4 IS NULL";

        try {

            Map<String, Object> param = Map.of("province",province);
            return jdbcTemplate.queryForObject(sql, param, Long.class);

        } catch (EmptyResultDataAccessException e) {
            log.error("locationCode를 찾을 수 없습니다.(with province)");
        }

        return null;
    }

    private LocalDateTime convertToLocalDateTime(long input) {
        return LocalDateTime.parse(Long.toString(input), DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }

    private AirConditionResponseDTO getAirConditionInfo(LocationDTO locationDTO){

        String stationName = getAirConditionObservatory(locationDTO);
        AirConditionResponseDTO result = airConditionClient.getAirConditionInfo(weatherKey, DATATYPE, stationName, DATATERM, 1.5);

        return result;
    }


    private String getAirConditionObservatory(LocationDTO locationDTO){

        Map<String, Double> transformedCoor = transformWGS84ToUTMK(locationDTO.getLongitude(), locationDTO.getLatitude());
        AirObservatoryResponseDTO result = airConditionClient.getObservatoryInfo(weatherKey, DATATYPE, transformedCoor.get("X"),transformedCoor.get("Y"), 1.2);

        return result.getResponse().getBody().getItems().get(0).getStationName();
    }

}