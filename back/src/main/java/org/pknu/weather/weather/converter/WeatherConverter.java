package org.pknu.weather.weather.converter;

import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.dto.WeatherRedisDTO;

import java.util.List;

public class WeatherConverter {

    // List<WeatherRedisDTO.WeatherData>를 Weather엔티티 리스트로 만드는 게 맞을지?
    // 엔티티 리스트가 아닌 weatherDTO 로 변환한다면 mainPageService에서 다시 변환과정을 거쳐야 하는 번거로움 발생하지만 컨버터를 정의해두면 반복작업은 없긴하다.
    public static List<Weather> toWeatherList(List<WeatherRedisDTO.WeatherData> weatherDataList) {
        return weatherDataList.stream()
                .map(WeatherConverter::toWeather)
                .toList();
    }

    private static Weather toWeather(WeatherRedisDTO.WeatherData weatherData) {
        return Weather.builder()
                .presentationTime(weatherData.getPresentationTime())
                .basetime(weatherData.getBasetime())
                .humidity(weatherData.getHumidity())
                .rain(weatherData.getRain())
                .rainType(weatherData.getRainType())
                .skyType(weatherData.getSkyType())
                .snowCover(weatherData.getSnowCover())
                .temperature(weatherData.getTemperature())
                .windSpeed(weatherData.getWindSpeed())
                .rainProb(weatherData.getRainProb())
                .sensibleTemperature(weatherData.getSensibleTemperature())
                .build();
    }

    public static List<WeatherRedisDTO.WeatherData> toWeatherDataList(List<Weather> weatherList) {
        return weatherList.stream()
                .map(WeatherConverter::toWeatherData)
                .toList();
    }

    public static WeatherRedisDTO.WeatherData toWeatherData(Weather weather) {
        return WeatherRedisDTO.WeatherData.builder()
                .presentationTime(weather.getPresentationTime())
                .basetime(weather.getBasetime())
                .windSpeed(weather.getWindSpeed())
                .humidity(weather.getHumidity())
                .rainProb(weather.getRainProb())
                .rain(weather.getRain())
                .rainType(weather.getRainType())
                .temperature(weather.getTemperature())
                .sensibleTemperature(weather.getSensibleTemperature())
                .snowCover(weather.getSnowCover())
                .skyType(weather.getSkyType())
                .build();
    }
}
