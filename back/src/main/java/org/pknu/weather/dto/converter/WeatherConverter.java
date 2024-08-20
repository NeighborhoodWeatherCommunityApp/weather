package org.pknu.weather.dto.converter;

import org.pknu.weather.common.WeatherRangeConverter;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.tag.RainTag;
import org.pknu.weather.domain.tag.TemperatureTag;

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

    public static WeatherResponseConverter.MainPageWeatherData toMainPageWeatherData(List<Weather> weatherList, Member member) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for(Weather w : weatherList) {
            max = Math.max(max, w.getTemperature());
            min = Math.min(min, w.getTemperature());
        }

        List<WeatherResponseConverter.WeatherPerHour> weatherPerHourList = weatherList.stream()
                .map(weather -> toWeatherPerHour(weather, member))
                .toList();

        Location location = weatherList.get(0).getLocation();
        Weather now = weatherList.get(0);

        return WeatherResponseConverter.MainPageWeatherData.builder()
                .location(location.getAddress())
                .currentSkyType(now.getSkyType())
                .currentTmp(now.getTemperature())
                .weatherPerHourList(weatherPerHourList)
                .temperature(toTemperature(max, min))
                .build();
    }

    public static WeatherResponseConverter.WeatherPerHour toWeatherPerHour(Weather weather, Member member) {
        RainTag rainTag = WeatherRangeConverter.rain2Text(weather.getRain());
        TemperatureTag temperatureTag = WeatherRangeConverter.tmp2Text(
                weather.getTemperature(), member.getSensitivity());

        return WeatherResponseConverter.WeatherPerHour.builder()
                .hour(weather.getPresentationTime())
                .skyType(weather.getSkyType())
                .rainAdverb(rainTag.getAdverb())
                .rainText(rainTag.getText())
                .rain(weather.getRain())
                .tmpAdverb(temperatureTag.getAdverb())
                .tmpText(temperatureTag.getText())
                .tmp(weather.getTemperature())
                .build();
    }

    public static WeatherResponseConverter.Temperature toTemperature(Integer max, Integer min) {
        return WeatherResponseConverter.Temperature.builder()
                .maxTmp(max)
                .minTmp(min)
                .build();
    }
}
