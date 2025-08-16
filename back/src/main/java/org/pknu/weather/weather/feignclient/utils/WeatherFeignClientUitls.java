package org.pknu.weather.weather.feignclient.utils;

import org.pknu.weather.weather.feignclient.dto.PointDTO;
import org.pknu.weather.weather.feignclient.dto.WeatherParams;

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
