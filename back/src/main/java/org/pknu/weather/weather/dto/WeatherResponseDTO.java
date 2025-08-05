package org.pknu.weather.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.domain.common.SkyType;

import java.time.LocalDateTime;
import java.util.List;

public class WeatherResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainPageWeatherData {
        String city;    // 시군구
        String street;  // 읍면동
        SkyType currentSkyType;
        Integer currentTmp;
        Double currentSensibleTmp;
        List<WeatherPerHour> weatherPerHourList;
        Temperature temperature;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherSimpleInfo {
        private List<String> tags;
        private String prcpProb;
        private List<SimpleWeatherPerDay> days;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class SimpleWeatherPerDay {
            private String day;
            private SkyType skyType;
        }
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtraWeatherInfo {
        LocalDateTime baseTime;
        Integer o3Grade;
        Integer pm10Grade;
        Integer pm10Value;
        Integer pm25Grade;
        Integer pm25Value;
        Integer uvGrade;
        Integer uvGradePlus3;
        Integer uvGradePlus6;
        Integer uvGradePlus9;
        Integer uvGradePlus12;
        Integer uvGradePlus15;
        Integer uvGradePlus18;
        Integer uvGradePlus21;
    }
      
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleRainInformation {
        String rainComment;
        String addComment;
        Boolean willRain;
        String rainfallAmount;
    }
}
