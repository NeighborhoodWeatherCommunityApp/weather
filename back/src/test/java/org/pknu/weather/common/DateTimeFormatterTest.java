
package org.pknu.weather.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
class DateTimeFormatterTest {

    @Test
    void 시간_차이를_문자로_반환하는_테스트() {
        //given
        LocalDateTime a = LocalDateTime.now();
        LocalDateTime minute = a.minusMinutes(1);
        LocalDateTime hour = a.minusHours(1);
        LocalDateTime day = a.minusDays(1);

        // when
        String m = DateTimeFormatter.pastTimeToString(minute);
        String h = DateTimeFormatter.pastTimeToString(hour);
        String d = DateTimeFormatter.pastTimeToString(day);

        // then
        Assertions.assertThat(m).isEqualTo("1분 전");
        Assertions.assertThat(h).isEqualTo("1시간 전");
        Assertions.assertThat(d).isEqualTo("1일 전");

    }
}
