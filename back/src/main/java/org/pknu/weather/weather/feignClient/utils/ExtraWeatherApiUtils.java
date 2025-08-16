package org.pknu.weather.weather.feignClient.utils;

import static org.pknu.weather.common.converter.CoordinateConverter.transformWGS84ToUTMK;
import static org.pknu.weather.common.formatter.DateTimeFormatter.getFormattedLocalDate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.location.dto.LocationDTO;
import org.pknu.weather.weather.dto.WeatherResponse;
import org.pknu.weather.weather.dto.WeatherResponse.ExtraWeatherInfo;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.weather.feignClient.AirConditionClient;
import org.pknu.weather.weather.feignClient.UVClient;
import org.pknu.weather.weather.feignClient.dto.AirConditionResponseDTO;
import org.pknu.weather.weather.feignClient.dto.AirObservatoryResponseDTO;
import org.pknu.weather.weather.feignClient.dto.UVResponseDTO;
import org.pknu.weather.weather.feignClient.dto.UVResponseDTO.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public WeatherResponse.ExtraWeatherInfo getExtraWeatherInfo(LocationDTO locationDTO) {

        Item uvResult = getUV(locationDTO);
        transferUvGrade(uvResult);

        AirConditionResponseDTO.Item airConditionInfo = getAirConditionInfo(locationDTO);

        return getExtraWeatherInformation(uvResult, airConditionInfo);
    }

    public WeatherResponse.ExtraWeatherInfo getExtraWeatherInfo(LocationDTO locationDTO, LocalDateTime baseTime) {

        Item uvResult = getUV(locationDTO);
        transferUvGrade(uvResult);

        if (baseTime.toLocalDate().isBefore(LocalDate.now())) {
            AirConditionResponseDTO.Item airConditionInfo = getAirConditionInfo(locationDTO);

            return getExtraWeatherInformation(uvResult, airConditionInfo);
        }

        return updateUvInformation(uvResult);
    }

    private Item getUV(LocationDTO locationDTO) {

        Long locationCode = getLocation(locationDTO);
        String date = getRequestDate();
        UVResponseDTO result = uvClient.getUVInfo(weatherKey, locationCode, date, DATATYPE);

        validateUvResult(result);

        return result.getResponse().getBody().getItems().getItem().get(0);
    }

    public void transferUvGrade(UVResponseDTO.Item uvResult) {
        int[] uvHours = {0, 3, 6, 9, 12, 15, 18, 21};

        for (int uvHour : uvHours) {
            try {
                updateUvGrade(uvResult, uvHour);
            } catch (NoSuchMethodException e) {
                log.error("메서드를 찾을 수 없습니다 H{}: {}", uvHour, e.getMessage());
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("해당 메서드에 문제가 발생했습니다. H{}: {}", uvHour, e.getMessage());
            }
        }
    }

    private void updateUvGrade(UVResponseDTO.Item uvResult, int uvHour)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Method getterMethod = uvResult.getClass().getMethod("getH" + uvHour);
        Integer currentValue = (Integer) getterMethod.invoke(uvResult);

        if (currentValue != null) {
            Integer newValue = calculateUvGrade(currentValue);

            Method setterMethod = uvResult.getClass().getMethod("setH" + uvHour, Integer.class);
            setterMethod.invoke(uvResult, newValue);
        }
    }

    private int calculateUvGrade(Integer value) {
        return Math.min(value / 3 + 1, 4);
    }

    private void validateUvResult(UVResponseDTO result) {
        if (result.getResponse().getHeader().getResultCode() != 0) {
            throw new GeneralException(ErrorStatus._API_SERVER_ERROR);
        }
    }

    private String getRequestDate() {
        return getFormattedLocalDate(LocalDate.now()) + LocalTime.now().getHour();
    }

    @Transactional(readOnly = true)
    public Long getLocation(LocationDTO locationDTO) {

        Long locationCode = getLocationCode(locationDTO.getProvince(), locationDTO.getCity(), locationDTO.getStreet());

        if (locationCode == null) {
            locationCode = getLocationCode(locationDTO.getProvince(), locationDTO.getCity());
        }

        if (locationCode == null) {
            locationCode = getLocationCode(locationDTO.getProvince());
        }

        if (locationCode == null) {
            throw new GeneralException(ErrorStatus._LOCATION_NOT_FOUND);
        }

        return locationCode;
    }

    private Long getLocationCode(String province, String city, String street) {

        String sql = "SELECT C1 FROM location_code WHERE C2 = :province AND C3 = :city AND C4 = :street";

        try {

            Map<String, Object> param = Map.of("province", province, "city", city, "street", street);
            return jdbcTemplate.queryForObject(sql, param, Long.class);

        } catch (EmptyResultDataAccessException e) {
            log.warn("locationCode를 찾을 수 없습니다.(with Street)");
        }

        return null;
    }

    private Long getLocationCode(String province, String city) {

        String sql = "SELECT C1 FROM location_code WHERE C2 = :province AND C3 = :city AND C4 IS NULL";

        try {

            Map<String, Object> param = Map.of("province", province, "city", city);
            return jdbcTemplate.queryForObject(sql, param, Long.class);

        } catch (EmptyResultDataAccessException e) {
            log.warn("locationCode를 찾을 수 없습니다.(with City)");
        }

        return null;
    }

    private Long getLocationCode(String province) {

        String sql = "SELECT C1 FROM location_code WHERE C2 = :province AND C3 IS NULL AND C4 IS NULL";

        try {

            Map<String, Object> param = Map.of("province", province);
            return jdbcTemplate.queryForObject(sql, param, Long.class);

        } catch (EmptyResultDataAccessException e) {
            log.error("locationCode를 찾을 수 없습니다.(with province)");
        }

        return null;
    }

    private LocalDateTime convertToLocalDateTime(long input) {
        return LocalDateTime.parse(Long.toString(input), DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }

    private AirConditionResponseDTO.Item getAirConditionInfo(LocationDTO locationDTO) {

        String stationName = getAirConditionObservatory(locationDTO);
        AirConditionResponseDTO result = airConditionClient.getAirConditionInfo(weatherKey, DATATYPE, stationName,
                DATATERM, 1.5);

        int resultCode = result.getResponse().getHeader().getResultCode();

        if (resultCode == 0) {
            return result.getResponse().getBody().getItems().get(0);
        } else {
            throw new GeneralException(ErrorStatus._API_SERVER_ERROR);
        }

    }


    private String getAirConditionObservatory(LocationDTO locationDTO) {

        Map<String, Double> transformedCoor = transformWGS84ToUTMK(locationDTO.getLongitude(),
                locationDTO.getLatitude());
        AirObservatoryResponseDTO result = airConditionClient.getObservatoryInfo(weatherKey, DATATYPE,
                transformedCoor.get("X"), transformedCoor.get("Y"), 1.2);

        return result.getResponse().getBody().getItems().get(0).getStationName();
    }

    private ExtraWeatherInfo getExtraWeatherInformation(Item uvResult, AirConditionResponseDTO.Item airConditionInfo) {
        return ExtraWeatherInfo.builder()
                .baseTime(convertToLocalDateTime(uvResult.getDate()))
                .o3Grade(airConditionInfo.getO3Grade())
                .pm10Grade(airConditionInfo.getPm10Grade())
                .pm10Value(checkNumberFormat(airConditionInfo.getPm10Value()))
                .pm25Grade(airConditionInfo.getPm25Grade())
                .pm25Value(checkNumberFormat(airConditionInfo.getPm25Value()))
                .uvGrade(uvResult.getH0())
                .uvGradePlus3(uvResult.getH3())
                .uvGradePlus6(uvResult.getH6())
                .uvGradePlus9(uvResult.getH9())
                .uvGradePlus12(uvResult.getH12())
                .uvGradePlus15(uvResult.getH15())
                .uvGradePlus18(uvResult.getH18())
                .uvGradePlus21(uvResult.getH21())
                .build();
    }

    private ExtraWeatherInfo updateUvInformation(Item uvResult) {
        return ExtraWeatherInfo.builder()
                .baseTime(convertToLocalDateTime(uvResult.getDate()))
                .uvGrade(uvResult.getH0())
                .uvGradePlus3(uvResult.getH3())
                .uvGradePlus6(uvResult.getH6())
                .uvGradePlus9(uvResult.getH9())
                .uvGradePlus12(uvResult.getH12())
                .uvGradePlus15(uvResult.getH15())
                .uvGradePlus18(uvResult.getH18())
                .uvGradePlus21(uvResult.getH21())
                .build();
    }

    private Integer checkNumberFormat(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
