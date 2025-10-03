package org.pknu.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.dto.WeatherResponseDTO;
import org.pknu.weather.weather.enums.RainType;
import org.pknu.weather.weather.enums.SkyType;

import java.time.LocalDateTime;

@Getter
public class TotalWeatherDto {
    private final WeatherDto weatherDto;
    private final ExtraWeatherDto extraWeatherDto;
    private final LocalDateTime baseTime;

    public TotalWeatherDto(Weather weather) {
        baseTime = weather.getBasetime();
        weatherDto = WeatherDto.builder()
                .location(weather.getLocation())
                .windSpeed(weather.getWindSpeed())
                .humidity(weather.getHumidity())
                .rainProb(weather.getRainProb())
                .rain(weather.getRain())
                .rainType(weather.getRainType())
                .temperature(weather.getTemperature())
                .sensibleTemperature(weather.getSensibleTemperature())
                .snowCover(weather.getSnowCover())
                .skyType(weather.getSkyType())
                .presentationTime(weather.getPresentationTime())
                .build();
        extraWeatherDto = new ExtraWeatherDto();
    }

    public TotalWeatherDto(Weather weather, WeatherResponseDTO.ExtraWeatherInfo extraWeatherInfo) {
        baseTime = weather.getBasetime();
        weatherDto = WeatherDto.builder()
                .location(weather.getLocation())
                .windSpeed(weather.getWindSpeed())
                .humidity(weather.getHumidity())
                .rainProb(weather.getRainProb())
                .rain(weather.getRain())
                .rainType(weather.getRainType())
                .temperature(weather.getTemperature())
                .sensibleTemperature(weather.getSensibleTemperature())
                .snowCover(weather.getSnowCover())
                .skyType(weather.getSkyType())
                .presentationTime(weather.getPresentationTime())
                .build();
        extraWeatherDto = ExtraWeatherDto.builder()
                .uv(extraWeatherInfo.getUvGrade())
                .uvPlus3(extraWeatherInfo.getUvGradePlus3())
                .uvPlus6(extraWeatherInfo.getUvGradePlus6())
                .uvPlus9(extraWeatherInfo.getUvGradePlus9())
                .uvPlus12(extraWeatherInfo.getUvGradePlus12())
                .uvPlus15(extraWeatherInfo.getUvGradePlus15())
                .uvPlus18(extraWeatherInfo.getUvGradePlus18())
                .uvPlus21(extraWeatherInfo.getUvGradePlus21())
                .o3(extraWeatherInfo.getO3Grade())
                .pm10(extraWeatherInfo.getPm10Grade())
                .pm25(extraWeatherInfo.getPm25Grade())
                .pm10value(extraWeatherInfo.getPm10Value())
                .pm25value(extraWeatherInfo.getPm25Value())
                .build();
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeatherDto {
        private Location location;
        private Double windSpeed;
        private Integer humidity;
        private Integer rainProb;
        private Float rain;
        private RainType rainType;
        private Integer temperature;
        private Double sensibleTemperature;
        private Float snowCover;
        private SkyType skyType;
        private LocalDateTime presentationTime;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExtraWeatherDto {
        private Integer uv;
        private Integer uvPlus3;
        private Integer uvPlus6;
        private Integer uvPlus9;
        private Integer uvPlus12;
        private Integer uvPlus15;
        private Integer uvPlus18;
        private Integer uvPlus21;
        private Integer o3;
        private Integer pm10;
        private Integer pm25;
        private Integer pm10value;
        private Integer pm25value;
    }
}
