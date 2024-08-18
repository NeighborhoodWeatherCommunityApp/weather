package org.pknu.weather.dto.converter;

import org.pknu.weather.common.WeatherRangeConverter;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.RainRange;
import org.pknu.weather.domain.common.TemperatureRange;

import java.util.List;

public class WeatherConverter {

//    public static Weather toWeather(Item item) {
//        return Weather.builder()
//                .basetime(item.getBaseTime())
//                .windSpeed(item.get)
//                .humidity()
//                .rainProb()
//                .rain()
//                .rainType()
//                .temperature()
//                .maxTemperature()
//                .minTemperature()
//                .snowCover()
//                .skyType()
//                .presentationTime()
//                .build();
//    }

    public static WeatherResponse.MainPageWeatherData toMainPageWeatherData(List<Weather> weatherList, Member member) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for(Weather w : weatherList) {
            max = Math.max(max, w.getTemperature());
            min = Math.min(min, w.getTemperature());
        }

        List<WeatherResponse.WeatherPerHour> weatherPerHourList = weatherList.stream()
                .map(weather -> toWeatherPerHour(weather, member))
                .toList();

        Location location = weatherList.get(0).getLocation();
        Weather now = weatherList.get(0);

        return WeatherResponse.MainPageWeatherData.builder()
                .location(location.getAddress())
                .currentSkyType(now.getSkyType())
                .currentTmp(now.getTemperature())
                .weatherPerHourList(weatherPerHourList)
                .temperature(toTemperature(max, min))
                .build();
    }

    public static WeatherResponse.WeatherPerHour toWeatherPerHour(Weather weather, Member member) {
        RainRange rainRange = WeatherRangeConverter.rain2Text(weather.getRain());
        TemperatureRange temperatureRange = WeatherRangeConverter.tmp2Text(
                weather.getTemperature(), member.getSensitivity());

        return WeatherResponse.WeatherPerHour.builder()
                .hour(weather.getPresentationTime())
                .skyType(weather.getSkyType())
                .rainAdverb(rainRange.getAdverb())
                .rainText(rainRange.getText())
                .rain(weather.getRain())
                .tmpAdverb(temperatureRange.getAdverb())
                .tmpText(temperatureRange.getText())
                .tmp(weather.getTemperature())
                .build();
    }

    public static WeatherResponse.Temperature toTemperature(Integer max, Integer min) {
        return WeatherResponse.Temperature.builder()
                .maxTmp(max)
                .minTmp(min)
                .build();
    }
}
