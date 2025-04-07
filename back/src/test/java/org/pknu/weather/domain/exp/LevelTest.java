package org.pknu.weather.domain.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LevelTest {

    @Test
    void 최대레벨메서드테스트() {
        Level maxLevel = Level.getMaxLevel();
        assertEquals(Level.LV6, maxLevel, "최고레벨은 6레벨");
    }
}
