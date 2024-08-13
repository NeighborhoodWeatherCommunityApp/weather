package org.pknu.weather.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class DateTimeFormatUtilsTest {

    @Test
    void DateTimeFormatUtils_정상_작동_테스트() {
        //given
        String formattedDate2YYMMDD = DateTimeFormaterUtils.getFormattedDate();
        String formattedTime2HHMM = DateTimeFormaterUtils.getFormattedTimeByThreeHour();

        log.info(formattedDate2YYMMDD);
        log.info(formattedTime2HHMM);

    }
}