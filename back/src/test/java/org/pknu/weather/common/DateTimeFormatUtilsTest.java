package org.pknu.weather.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class DateTimeFormatUtilsTest {

    @Test
    void DateTimeFormatUtils_정상_작동_테스트() {
        //given
        String formattedDate2YYMMDD = DateTimeFormatUtils.getFormattedDate2YYYYMMDD();
        String formattedTime2HHMM = DateTimeFormatUtils.getFormattedTime2HHMM();

        log.info(formattedDate2YYMMDD);
        log.info(formattedTime2HHMM);

    }
}