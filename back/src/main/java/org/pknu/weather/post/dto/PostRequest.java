package org.pknu.weather.post.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.post.tag.enums.DustTag;
import org.pknu.weather.post.tag.enums.HumidityTag;
import org.pknu.weather.post.tag.enums.SkyTag;
import org.pknu.weather.post.tag.enums.TemperatureTag;
import org.pknu.weather.post.tag.enums.WindTag;

public class PostRequest {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HobbyParams {
        Long locationId;
        String content;
        String postType;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        @Size(max = 300)    // TODO: 최대 size 정해야함
        private String content;

        @Min(value = 1, message = "temperatureTagCode 는 1 ~ 10 사이의 값이어야 합니다.")
        @Max(value = 10, message = "temperatureTagCode 는 1 ~ 10 사이의 값이어야 합니다.")
        private Integer temperatureTagCode;

        @Min(value = 1, message = "skyTagCode 는 1 ~ 4 사이의 값이어야 합니다.")
        @Max(value = 4, message = "skyTagCode 는 1 ~ 4 사이의 값이어야 합니다.")
        private Integer skyTagCode;

        @Min(value = 1, message = "humidityTagCode 는 1 ~ 5 사이의 값이어야 합니다.")
        @Max(value = 5, message = "humidityTagCode 는 1 ~ 5 사이의 값이어야 합니다.")
        private Integer humidityTagCode;

        @Min(value = 1, message = "windTagCode 는 1 ~ 3 사이의 값이어야 합니다.")
        @Max(value = 3, message = "windTagCode 는 1 ~ 3 사이의 값이어야 합니다.")
        private Integer windTagCode;

        @Min(value = 1, message = "dustTagCode 는 1 ~ 5 사이의 값이어야 합니다.")
        @Max(value = 5, message = "dustTagCode 는 1 ~ 5 사이의 값이어야 합니다.")
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

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatePostAndTagParameters {
        @Size(max = 300)
        private String content;

        private String temperatureTagKey;

        private String skyTagKey;

        private String humidityTagKey;

        private String windTagKey;

        private String dustTagKey;

        public boolean contentIsEmpty() {
            return this.content == null || this.content.isBlank();
        }

        public boolean tagKeyStringIsEmpty() {
            return (this.dustTagKey == null || this.dustTagKey.isBlank()) &&
                    (this.humidityTagKey == null || this.humidityTagKey.isBlank()) &&
                    (this.skyTagKey == null || this.skyTagKey.isBlank()) &&
                    (this.windTagKey == null || this.windTagKey.isBlank()) &&
                    (this.temperatureTagKey == null || this.temperatureTagKey.isBlank());
        }

        public boolean parametersIsEmpty() {
            return contentIsEmpty() && tagKeyStringIsEmpty();
        }
    }
}
