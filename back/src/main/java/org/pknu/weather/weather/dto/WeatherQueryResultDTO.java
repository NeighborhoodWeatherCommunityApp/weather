package org.pknu.weather.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class WeatherQueryResultDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleRainInfo {
        LocalDateTime time;
        Integer rainProbability;
        Float rain;
        Float snowCover;
    }
}
