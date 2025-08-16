package org.pknu.weather.post.tag.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.weather.dto.TotalWeatherDto;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor
public enum HumidityTag implements EnumTag {
    DRY("", "건조함", 1),
    PLEASANT("", "쾌적함", 2),
    LITTLE_HUMID("약간", "습함", 3),
    HUMID("", "습함", 4),
    VERY_HUMID("매우", "습함", 5);

    private final String adverb;
    private final String text;
    private final Integer code;

    @Override
    public EnumTag findByCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findAny()
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public EnumTag weatherValueToTag(TotalWeatherDto totalWeatherDto) {
        Integer temperature = totalWeatherDto.getWeatherDto().getTemperature();
        Integer humidity = totalWeatherDto.getWeatherDto().getHumidity();

        if (temperature <= 15) {
            if (humidity <= 50) {
                return DRY;
            } else if (humidity <= 70) {
                return PLEASANT;
            } else if (humidity <= 90) {
                return LITTLE_HUMID;
            } else {
                return HUMID;
            }
        } else if (temperature <= 20) {
            if (humidity <= 40) {
                return DRY;
            } else if (humidity <= 60) {
                return PLEASANT;
            } else if (humidity <= 80) {
                return LITTLE_HUMID;
            } else {
                return HUMID;
            }
        } else if (temperature <= 23) {
            if (humidity <= 30) {
                return DRY;
            } else if (humidity <= 50) {
                return PLEASANT;
            } else if (humidity <= 70) {
                return LITTLE_HUMID;
            } else if (humidity <= 90) {
                return HUMID;
            } else {
                return VERY_HUMID;
            }
        } else {
            if (humidity <= 20) {
                return DRY;
            } else if (humidity <= 40) {
                return PLEASANT;
            } else if (humidity <= 60) {
                return LITTLE_HUMID;
            } else if (humidity <= 80) {
                return HUMID;
            } else {
                return VERY_HUMID;
            }
        }
    }
}
