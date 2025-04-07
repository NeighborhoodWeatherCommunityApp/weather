package org.pknu.weather.domain.exp;

import java.util.Arrays;
import java.util.Comparator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LevelTest {
    @Test
    void 최대레벨메서드테스트() {
        // given
        Level maxLevel = Arrays.stream(Level.values())
                .max(Comparator.comparingInt(Level::getLevelNumber))
                .get();

        // when
        Level getMaxLevel = Level.getMaxLevel();

        // then
        Assertions.assertThat(maxLevel).isEqualTo(getMaxLevel);
    }
}
