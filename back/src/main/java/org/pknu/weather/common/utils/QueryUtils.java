package org.pknu.weather.common.utils;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.weather.QWeather;

import java.time.LocalDateTime;

public class QueryUtils {
    /**
     * weather의 예보 시간이 24시간 이내인지 검사
     * ex) weather.presentationTIme이 01-01 00:00:00 ~ yyyy-MM-02 00:00:00 사이의 값
     * @param weather
     * @return
     */
    public static BooleanExpression presentationTimeWithinLast24Hours(QWeather weather) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(24);

        return weather.presentationTime.between(now, end);
    }

    public static BooleanExpression isSameLocation(Location location, QWeather weather) {
        return weather.location.eq(location);
    }

}
