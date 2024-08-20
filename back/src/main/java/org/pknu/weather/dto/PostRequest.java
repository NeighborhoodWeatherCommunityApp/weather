package org.pknu.weather.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.domain.tag.*;

public class PostRequest {


    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        @Size(max = 300)    // TODO: 최대 size 정해야함
        private String content;

        @Min(value = 0, message = "temperatureTagCode 는 0 ~ 9 사이의 값이어야 합니다.")
        @Max(value = 9, message = "temperatureTagCode 는 0 ~ 9 사이의 값이어야 합니다.")
        private Integer temperatureTagCode;

        @Min(value = 0, message = "skyTagCode 는 0 ~ 4 사이의 값이어야 합니다.")
        @Max(value = 4, message = "skyTagCode 는 0 ~ 4 사이의 값이어야 합니다.")
        private Integer skyTagCode;

        @Min(value = 0, message = "humidityTagCode 는 0 ~ 4 사이의 값이어야 합니다.")
        @Max(value = 4, message = "humidityTagCode 는 0 ~ 4 사이의 값이어야 합니다.")
        private Integer humidityTagCode;

        @Min(value = 0, message = "windTagCode 는 0 ~ 2 사이의 값이어야 합니다.")
        @Max(value = 2, message = "windTagCode 는 0 ~ 2 사이의 값이어야 합니다.")
        private Integer windTagCode;

        @Min(value = 0, message = "dustTagCode 는 0 ~ 4 사이의 값이어야 합니다.")
        @Max(value = 4, message = "dustTagCode 는 0 ~ 4 사이의 값이어야 합니다.")
        private Integer dustTagCode;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatePost {
        private String content;
        private TemperatureTag temperatureTag;
        private SkyTag skyTag;
        private HumidityTag humidityTag;
        private WindTag windTag;
        private DustTag dustTag;
    }
}
