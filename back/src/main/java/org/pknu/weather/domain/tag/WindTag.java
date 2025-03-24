package org.pknu.weather.domain.tag;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.dto.TotalWeatherDto;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor
public enum WindTag implements EnumTag {
    NONE("안불어요", 1),
    WEAK_WINDY("조금 불어요", 2),
    NORMAL_WINDY("보통이에요", 3),
    VERY_WINDY("많이 불어요", 4);

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
    public WindTag weatherValueToTag(TotalWeatherDto totalWeatherDto) {
        Double windSpeed = totalWeatherDto.getWeatherDto().getWindSpeed();

        if (windSpeed < 1.5) {
            return WindTag.NONE;
        } else if (windSpeed < 8.0) {
            return WindTag.NORMAL_WINDY;
        } else if (windSpeed < 10.8) {
            return WindTag.WEAK_WINDY;
        } else {
            return WindTag.VERY_WINDY;
        }
    }
}
