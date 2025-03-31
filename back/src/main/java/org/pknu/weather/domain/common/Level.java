package org.pknu.weather.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {
    LV1(1, 0, "쌔싹"),
    LV2(2, 100, "바람"),
    LV3(3, 1000, "구름"),
    LV4(4, 5000, "비"),
    LV5(5, 10000, "번개"),
    LV6(6, 20000, "태풍"),
    ;

    private final Integer levelValue;
    private final Integer requiredExp;
    private final String title;

    public static Level getMaxLevel() {
        Level[] values = Level.values();
        return values[values.length - 1];
    }
}
