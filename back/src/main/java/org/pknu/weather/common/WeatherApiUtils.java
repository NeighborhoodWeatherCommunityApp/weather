package org.pknu.weather.common;

import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherApiResponse;
import org.pknu.weather.dto.WeatherApiResponse.Response.Body.Items.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherApiUtils {
    public static Map<String, Weather> responseProcess(List<Item> itemList, String date, String time) {
        Map<String, Weather> weatherMap = new HashMap<>();
        LocalDateTime baseTime = DateTimeFormaterUtils.formattedDateTime2LocalDateTime(date, time);

        for (Item item : itemList) {
            String fcstTime = item.getFcstTime();
            LocalDateTime presentationTime = DateTimeFormaterUtils.formattedDateTime2LocalDateTime(date, fcstTime);

            if(presentationTime.isBefore(baseTime)) {
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

        return weatherMap;
    }
}
