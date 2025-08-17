package org.pknu.weather.tag.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.weather.dto.TotalWeatherDto;
import org.pknu.weather.weather.dto.TotalWeatherDto.ExtraWeatherDto;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor
public enum DustTag implements EnumTag {
    GOOD("", "좋음", 1),
    NORMAL("", "보통", 2),
    LITTLE_BAD("", "나쁨", 3),
    VERY_BAD("매우", "나쁨", 4);

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
        return pmValueToDustTag(totalWeatherDto.getExtraWeatherDto());
    }

    private DustTag pmValueToDustTag(ExtraWeatherDto extraWeatherDto) {
        Integer pm10Value = extraWeatherDto.getPm10value();
        Integer pm25Value = extraWeatherDto.getPm25value();

        DustTag pm10;
        DustTag pm25;

        if (pm10Value <= 30) {
            pm10 = DustTag.GOOD;
        } else if (pm10Value <= 50) {
            pm10 = DustTag.NORMAL;
        } else if (pm10Value <= 150) {
            pm10 = DustTag.LITTLE_BAD;
        } else {
            pm10 = DustTag.VERY_BAD;
        }

        if (pm25Value <= 30) {
            pm25 = DustTag.GOOD;
        } else if (pm25Value <= 50) {
            pm25 = DustTag.NORMAL;
        } else if (pm25Value <= 150) {
            pm25 = DustTag.LITTLE_BAD;
        } else {
            pm25 = DustTag.VERY_BAD;
        }

        if (pm10.getCode() > pm25.getCode()) {
            return pm10;
        } else {
            return pm25;
        }
    }
}

