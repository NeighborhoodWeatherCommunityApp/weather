package org.pknu.weather.common;

import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherApiResponse;
import org.pknu.weather.dto.WeatherApiResponse.Response.Body.Items.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherApiUtils {
    public static Map<String, Weather> responseProcess(List<Item> itemList, String baseDate, String baseTime) {
        Map<String, Weather> weatherMap = new HashMap<>();

        for (Item item : itemList) {
            String fcstTime = item.getFcstTime();

            if (!weatherMap.containsKey(fcstTime)) {
                Weather weather = Weather.builder()
                        .basetime(DateTimeFormaterUtils.formattedDateTime2LocalDateTime(baseDate, baseTime))
                        .presentationTime(DateTimeFormaterUtils.formattedDateTime2LocalDateTime(baseDate, fcstTime))
                        .build();

                weatherMap.put(fcstTime, weather);
            }

            Weather weather = weatherMap.get(fcstTime);
            weather.categoryClassify(item);
            weatherMap.put(fcstTime, weather);
        }

        return weatherMap;
    }
}
