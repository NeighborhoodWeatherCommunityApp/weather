package org.pknu.weather.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.weather.enums.RainType;
import org.pknu.weather.weather.enums.SkyType;

import java.time.LocalDateTime;

public class WeatherRedisDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherData {
        LocalDateTime presentationTime;
        LocalDateTime basetime;
        Double windSpeed;
        Integer humidity;
        Integer rainProb;
        Float rain;
        RainType rainType;
        Integer temperature;
        Double sensibleTemperature;
        Float snowCover;
        SkyType skyType;
    }
}
