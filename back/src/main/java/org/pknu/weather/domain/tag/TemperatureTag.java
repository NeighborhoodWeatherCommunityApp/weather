package org.pknu.weather.domain.tag;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.dto.TotalWeatherDto;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor
public enum TemperatureTag implements EnumTag {
    VERY_COLD("매우", "추움", 1),
    COLD("", "추움", 2),
    LITTLE_COLD("조금", "추움", 3),
    COOL("", "선선", 4),
    COMMON("", "보통", 5),
    WARM("", "따뜻", 6),
    LITTLE_WARM("조금", "따뜻", 7),
    LITTLE_HOT("조금", "더움", 8),
    HOT("", "더움", 9),
    VERY_HOT("매우", "더움", 10);

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
        return null;
    }
}
