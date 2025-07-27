package org.pknu.weather.feignClient.utils;

import org.pknu.weather.feignClient.dto.PointDTO;
import org.pknu.weather.feignClient.dto.WeatherParams;

public class WeatherFeignClientUitls {
    public static WeatherParams create(String serviceKey, String baseDate, String baseTime, PointDTO pointDTO) {
        return WeatherParams.builder()
                .serviceKey(serviceKey)
                .pageNo(1)
                .numOfRows(288)
                .base_date(baseDate)
                .base_time(baseTime)
                .nx(pointDTO.getX())
                .ny(pointDTO.getY())
                .build();
    }
}
