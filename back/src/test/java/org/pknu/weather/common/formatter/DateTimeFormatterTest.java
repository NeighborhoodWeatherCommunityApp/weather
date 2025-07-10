package org.pknu.weather.common.formatter;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DateTimeFormatterTest {

    static Stream<LocalDateTime> provideTestDates() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                now.minusDays(1).withHour(23).withMinute(10),
                now.withHour(0).withMinute(10),
                now.withHour(1).withMinute(10),
                now.withHour(2).withMinute(10),
                now.withHour(3).withMinute(10),
                now.withHour(4).withMinute(10)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestDates")
    void 날짜가_변경되는_시점에서도_baseTime을_정상적으로_반환하는지_테스트(LocalDateTime localDateTime) {
        // given
        String baseDate = DateTimeFormatter.getFormattedBaseDate(localDateTime);
        String baseTime = DateTimeFormatter.getFormattedBaseTime(localDateTime);

        // when
        String baseDateResult = DateTimeFormatter.getFormattedBaseDate(localDateTime);
        String baseTimeResult = DateTimeFormatter.getFormattedBaseTime(localDateTime);

        // then
        Assertions.assertThat(baseDate).isEqualTo(baseDateResult);
        Assertions.assertThat(baseTime).isEqualTo(baseTimeResult);
    }
}
