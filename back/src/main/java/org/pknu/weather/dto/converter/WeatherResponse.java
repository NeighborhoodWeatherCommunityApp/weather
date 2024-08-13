package org.pknu.weather.dto.converter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WeatherResponse {


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Save {
        String message;
    }
}
