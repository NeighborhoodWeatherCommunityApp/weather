package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum WindTag implements EnumTag {
    NONE("안불어요", 1),
    WINDY("조금 불어요", 2),
    VERY_WINDY("많이 불어요", 3);

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
}
