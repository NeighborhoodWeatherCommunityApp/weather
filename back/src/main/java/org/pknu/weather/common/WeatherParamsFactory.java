package org.pknu.weather.common;

import org.pknu.weather.dto.Point;
import org.pknu.weather.dto.WeatherParams;

public class WeatherParamsFactory {
    public static WeatherParams create(String baseDate, String baseTime, Point point) {
        return WeatherParams.builder()
                .base_date(baseDate)
                .base_time(baseTime)
                .nx(point.getX())
                .ny(point.getY())
                .build();
    }
}
