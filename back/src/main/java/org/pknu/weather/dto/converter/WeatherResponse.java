package org.pknu.weather.dto.converter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.domain.common.SkyType;

import java.time.LocalDateTime;
import java.util.List;

public class WeatherResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainPageWeatherData {
        String location;
        SkyType currentSkyType;
        Integer currentTmp;
        List<WeatherPerHour> weatherPerHourList;
        Temperature temperature;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Temperature {
        Integer maxTmp;
        Integer minTmp;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherPerHour {
        LocalDateTime hour;
        SkyType skyType;
        String rainAdverb;      // 조금, 많이 등의 부사
        String rainText;       // 강수량의 텍스트화
        Float rain;            // 강수량
        String tmpAdverb;       // 조금, 많이 등의 부사
        String tmpText;         // 온도의 텍스트화
        Integer tmp;       // 온도
    }
}
