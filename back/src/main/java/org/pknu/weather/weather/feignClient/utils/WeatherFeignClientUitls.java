package org.pknu.weather.weather.feignClient.utils;

import org.pknu.weather.common.feignClient.dto.PointDTO;
import org.pknu.weather.weather.feignClient.dto.WeatherParams;

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
