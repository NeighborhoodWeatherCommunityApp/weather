package org.pknu.weather.domain.tag;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.weather.dto.TotalWeatherDTO;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor
public enum SkyTag implements EnumTag {
    RAIN("비와요", 1),
    CLOUDY("흐려요", 2),
    CLEAR_AND_CLOUDY("맑고 구름이 많아요", 3),
    CLEAR("맑아요", 4);

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
    public EnumTag weatherValueToTag(TotalWeatherDTO totalWeatherDto) {
        return CLEAR;
    }
}
