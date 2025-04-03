package org.pknu.weather.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {
    LV1(1, 0L, "쌔싹"),
    LV2(2, 100L, "바람"),
    LV3(3, 1000L, "구름"),
    LV4(4, 5000L, "비"),
    LV5(5, 10000L, "번개"),
    LV6(6, 20000L, "태풍"),
    ;

    private final Integer level;
    private final Long requiredExp;
    private final String title;

    public static Level getMaxLevel() {
        Level[] values = Level.values();
        return values[values.length - 1];
    }

    public static Level getNextLevel(Level currentLevel) {
        Level[] levels = Level.values();
        int currentLevelValue = currentLevel.ordinal();

        if (currentLevelValue + 1 < levels.length) {
            return levels[currentLevelValue + 1];
        }

        return currentLevel;
    }
}
