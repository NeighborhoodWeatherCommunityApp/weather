package org.pknu.weather.feignClient.utils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.common.WeatherParamsFactory;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherApiResponse;
import org.pknu.weather.dto.WeatherApiResponse.Response.Body.Items.Item;
import org.pknu.weather.feignClient.WeatherFeignClient;
import org.pknu.weather.feignClient.dto.PointDTO;
import org.pknu.weather.feignClient.dto.WeatherParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherFeignClientUtils {

    private final WeatherFeignClient weatherFeignClient;

    @Value("${api.weather.service-key}")
    private String weatherServiceKey;

    /**
     * 사용자의 위도 경도 및 기타 정보를 받아와 weather로 반환한다.
     *
     * @return now ~ 24 시간의 Wether 엔티티를 담고있는 List
     * @Location 사용자 위치 엔티티
     */
    public List<Weather> getVillageShortTermForecast(Location location) {
        float lon = location.getLongitude().floatValue();
        float lat = location.getLatitude().floatValue();

        PointDTO pointDTO = GeometryUtils.coordinateToPoint(lon, lat);
        String date = DateTimeFormatter.getFormattedBaseDate();
        String time = DateTimeFormatter.getFormattedBaseTime();

        WeatherParams weatherParams = WeatherParamsFactory.create(weatherServiceKey, date, time, pointDTO);
        log.info(String.format("\t\t x:%s y:%s date:%s time:%s"), pointDTO.getX(), pointDTO.getY(), date, time);

        WeatherApiResponse weatherApiResponse = weatherFeignClient.getVillageShortTermForecast(weatherParams);
        List<WeatherApiResponse.Response.Body.Items.Item> itemList = weatherApiResponse.getResponse()
                .getBody()
                .getItems()
                .getItemList();

        return WeatherFeignClientUtils.responseProcess(itemList, date, time);
    }

    /**
     * 단기 날씨 예보 API 에서 얻은 데이터를 Weather 데이터로 가공하는 메서드
     *
     * @param itemList 단기 날씨 예보 API 에서 얻은 데이터
     * @param date     ex. "202409"
     * @param time     ex. "0500"
     * @return
     */
    public static List<Weather> responseProcess(List<Item> itemList, String date, String time) {
        Map<String, Weather> weatherMap = new HashMap<>();
        LocalDateTime baseTime = DateTimeFormatter.formattedDateTime2LocalDateTime(date, time);

        for (Item item : itemList) {
            String fcstTime = item.getFcstTime();
            LocalDateTime presentationTime = DateTimeFormatter.formattedDateTime2LocalDateTime(date, fcstTime);

            if (presentationTime.isBefore(baseTime)) {
                presentationTime = presentationTime.plusDays(1L);
            }

            if (!weatherMap.containsKey(fcstTime)) {
                Weather weather = Weather.builder()
                        .basetime(baseTime)
                        .presentationTime(presentationTime)
                        .build();

                weatherMap.put(fcstTime, weather);
            }

            Weather weather = weatherMap.get(fcstTime);
            weather.categoryClassify(item);
            weatherMap.put(fcstTime, weather);
        }

        return weatherMap.values().stream()
                .filter(weather -> weather.getPresentationTime().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList();
    }
}
