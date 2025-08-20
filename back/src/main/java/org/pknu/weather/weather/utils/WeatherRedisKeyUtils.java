package org.pknu.weather.weather.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WeatherRedisKeyUtils {
    /**
     * Weather을 캐싱하는 데이터의 key 생성을 담당합니다. 재사용성을 고려해 Utils로 분류
     * weather:location:{location_id}:presentation_time:{presentation_time} 형태
     */
    public static String buildKey(Long locationId, LocalDateTime presentationTime) {
        return String.format("weather:location:%d:presentation_time:%s", locationId, presentationTime.toString());
    }

    public static String buildKey(Long locationId) {
        return String.format("weather:location:%d", locationId);
    }

    /**
     * 특정 location에 대하여 startTime부터 hours 만큼 1시간 단위로 redis key를 생성하여 반환합니다.
     */
    public static List<String> generateHourlyWeatherKeys(Long locationId, LocalDateTime startTime, int hours) {
        LocalDateTime startTimeOnTime = startTime
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < hours; i++) {
            keys.add(buildKey(locationId, startTimeOnTime.plusHours(i)));
        }

        return keys;
    }
}
