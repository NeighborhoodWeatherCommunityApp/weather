package org.pknu.weather.common;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public final class DateTimeFormatUtils {

    /**
     * LocalDate를 yyyyMMdd 형태로 반환합니다.
     *
     * @return yyyyMMdd, String 형태의 formatted date
     */
    public static String getFormattedDate2YYYYMMDD() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return currentDate.format(formatter);
    }

    /**
     * LocalTime을 HHmm 형태로 반환합니다.
     *
     * @return HHmm, String 형태의 formatted date
     */
    public static String getFormattedTime2HHMM() {
        LocalTime currentTime = getClosestTimeToPresent(LocalTime.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        return currentTime.format(formatter);
    }

    /**
     * 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 중 현재보다 과거이면서 가장가까운 값을 반환합니다.
     */
    private static LocalTime getClosestTimeToPresent(LocalTime currentTime) {
        List<LocalTime> predefinedTimes = Arrays.asList(
                LocalTime.of(2, 0),
                LocalTime.of(5, 0),
                LocalTime.of(8, 0),
                LocalTime.of(11, 0),
                LocalTime.of(14, 0),
                LocalTime.of(17, 0),
                LocalTime.of(20, 0),
                LocalTime.of(23, 0)
        );

        LocalTime closestPastTime = predefinedTimes.get(0);
        for (LocalTime time : predefinedTimes) {
            if (time.isBefore(currentTime)) {
                closestPastTime = time;
            } else {
                break;
            }
        }

        return closestPastTime;
    }
}
