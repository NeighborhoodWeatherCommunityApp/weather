package org.pknu.weather.common;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public final class DateTimeFormaterUtils {

    /**
     * post의 생성 시각과 현재와의 차이를 String으로 반환합니다.
     *
     * @param createdAt post의 생성 시각
     * @return
     */
    public static String pastTimeToString(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime comparison = createdAt;
        Duration diff = Duration.between(createdAt, now);

        if (comparison.plusHours(1).isBefore(now)) {
            return diff.toMinutes() + "분 전";
        } else {
            return diff.toHours() + "시간 전";
        }
    }

    /**
     * LocalDate를 yyyyMMdd 형태로 반환합니다.
     *
     * @return yyyyMMdd, String 형태의 formatted date
     */
    public static String getFormattedDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return currentDate.format(formatter);
    }

    public static String getFormattedDate(LocalDate currentDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return currentDate.format(formatter);
    }

    /**
     * LocalTime을 HHmm 형태로 반환합니다.
     * 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 중 현재보다 과거이면서 가장가까운 값을 반환합니다.
     *
     * @return HHmm, String 형태의 formatted time
     */
    public static String getFormattedTimeByThreeHour() {
        LocalTime currentTime = getClosestTimeToPresent(LocalTime.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        return currentTime.format(formatter);
    }

    /**
     * LocalTime을 HHmm 형태로 반환합니다. 1시간 단위로 반환합니다.
     *
     * @return HHmm, String 형태의 formatted time
     */
    public static String getFormattedTimeByOneHour() {
        LocalTime currentTime = LocalTime.now().withMinute(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        return currentTime.format(formatter);
    }

    /**
     * LocalTime을 HHmm 형태로 반환합니다. 1시간 단위로 반환합니다.
     *
     * @return HHmm, String 형태의 formatted time
     */
    public static String getFormattedTimeByOneHour(LocalTime now) {
        LocalTime currentTime = now.withMinute(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        return currentTime.format(formatter);
    }

    /**
     * yyyyMMDD HHmm 을 LocalDateTime으로 변경
     *
     * @param date
     * @param time
     * @return
     */
    public static LocalDateTime formattedDateTime2LocalDateTime(String date, String time) {
        String dateTime = date + time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return LocalDateTime.parse(dateTime, formatter);
    }

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
