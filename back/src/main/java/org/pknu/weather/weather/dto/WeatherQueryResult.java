package org.pknu.weather.weather.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WeatherQueryResult {
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
