package org.pknu.weather.common.formatter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public final class DateTimeFormatter {
    static java.time.format.DateTimeFormatter yyyyMMdd = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
    static java.time.format.DateTimeFormatter hhmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
    static java.time.format.DateTimeFormatter yyyyMMddHHmm = java.time.format.DateTimeFormatter.ofPattern(
            "yyyyMMddHHmm");

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

        if (comparison.plusDays(1).isBefore(now)) {
            return diff.toDays() + "일 전";
        } else if (comparison.plusHours(1).isBefore(now)) {
            return diff.toHours() + "시간 전";
        } else {
            return diff.toMinutes() + "분 전";
        }
    }

    /**
     * 현재 LocalDate를 yyyymmdd 로 변환한다.
     *
     * @return string yyyyMMdd
     */
    public static String getFormattedLocalDate(LocalDate currentDate) {
        return currentDate.format(yyyyMMdd);
    }

    /**
     * basetime(0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300)에 해당하는 LocalDate 값을 yyyyMMdd 형태로 반환합니다. basedate을 계산할 때
     * 사용한다.
     * <p>
     * 주의 00시 ~ 1시59분 사이에는 LocalDateTime.minusDay(1) 의 값이 변환된 스트링이 반환된다.
     *
     * @return yyyyMMdd 형태의 날짜 스트링
     */
    public static String getFormattedBaseDate(LocalDateTime localDateTime) {
        LocalDateTime currentLocalDateTime = getBaseLocalDateTime(localDateTime);
        return currentLocalDateTime.toLocalDate().format(yyyyMMdd);
    }

    /**
     * LocalTime을 HHmm 형태로 반환합니다. 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 중에서
     *
     * @param localDateTime 보다 과거이면서 가장가까운 값을 반환합니다.
     * @return HHmm (ex. 0200)
     */
    public static String getFormattedBaseTime(LocalDateTime localDateTime) {
        LocalDateTime currentLocalDateTime = getBaseLocalDateTime(localDateTime);
        return currentLocalDateTime.format(hhmm);
    }

    /**
     * LocalTime을 HHmm 형태로 반환합니다. 1시간 단위로 반환합니다.
     *
     * @return HHmm, String 형태의 formatted time
     */
    public static String getFormattedTimeByOneHour(LocalTime now) {
        LocalTime currentTime = now.withMinute(0);
        return currentTime.format(hhmm);
    }

    /**
     * yyyyMMdd HHmm 을 LocalDateTime으로 변경
     *
     * @param date yyyyMMdd
     * @param time HHmm
     * @return LocalDateTime
     */
    public static LocalDateTime formattedDateTime2LocalDateTime(String date, String time) {
        return LocalDateTime.parse(date + time, yyyyMMddHHmm);
    }

    /**
     * 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 시 중 현재보다 과거이면서 가장가까운 LocalDateTime을 반환합니다.
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getBaseLocalDateTime(LocalDateTime currentLocalDateTime) {
        return findLocalDateTimeCloseToNow(currentLocalDateTime);
    }

    private static LocalDateTime findLocalDateTimeCloseToNow(LocalDateTime currentLocalDateTime) {
        List<LocalDateTime> predefinedTimes = getPredefinedTimes(currentLocalDateTime);

        LocalDateTime closestPastTime = predefinedTimes.get(0);
        for (LocalDateTime time : predefinedTimes) {
            if (time.isBefore(currentLocalDateTime) || time.isEqual(currentLocalDateTime)) {
                closestPastTime = time;
            } else {
                break;
            }
        }

        return closestPastTime;
    }

    private static List<LocalDateTime> getPredefinedTimes(LocalDateTime currentLocalDateTime) {
        LocalDate currentLocalDate = currentLocalDateTime.toLocalDate();

        return Arrays.asList(
                LocalDateTime.of(currentLocalDate.minusDays(1), LocalTime.of(23, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(2, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(5, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(8, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(11, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(14, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(17, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(20, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(23, 0))
        );
    }
}
