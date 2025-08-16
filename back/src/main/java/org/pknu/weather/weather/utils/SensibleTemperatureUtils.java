package org.pknu.weather.weather.utils;

import java.time.LocalDateTime;

/**
 * 체감 온도를 계산 하는 유틸 클래스
 */
public class SensibleTemperatureUtils {

    /**
     * 겨울철 체감온도 (기온이 10도 이하, 풍속이 1.3m/s 이하일 때 작동)
     *
     * @param ta 기온
     * @param v  10분 평균 풍속, 그냥 풍속으로 계싼해도 무방함
     */
    private static double getWinterSensibleTemperature(double ta, double v) {
        double vKmh = v * 3.6;
        double vKmhPow = Math.pow(vKmh, 0.16);
        return 13.12 + (0.6215 * ta) - (11.37 * vKmhPow) + (0.3965 * Math.pow(vKmh, 0.16) * ta);
    }

    /**
     * 여름철 체감온도
     *
     * @param ta 기온
     * @param rh 상대습도
     */
    private static double getSummerSensibleTemperature(double ta, double rh) {
        double tw = getTw(ta, rh);
        return -0.2442 + (0.55399 * tw) + (0.45535 * ta) - (0.0022 * Math.pow(tw, 2.0)) + (0.00278 * tw * ta) + 3.0;
    }

    /**
     * 지금이 몇 월인지에 따라 여름 및 겨울 계산공식 적용
     *
     * @param ta 기온
     * @param rh 상대습도
     * @param v  10분 평균 풍속(Km/s), 그냥 풍속으로 계싼해도 무방함
     * @return 체감온도
     */
    public static double getSensibleTemperature(double ta, double rh, double v) {
        if (ta <= 10 && v >= 1.3) {
            return getWinterSensibleTemperature(ta, v);
        }

        if (ta >= 25) {
            return getSummerSensibleTemperature(ta, rh);
        }

        return ta;
    }


    /**
     * 습구온도 계산공식
     *
     * @param ta 온도
     * @param rh 상대습도
     */
    private static double getTw(double ta, double rh) {
        return ta * Math.atan(0.151977 * Math.pow(rh + 8.313659, 0.5)) +
                Math.atan(ta + rh) - Math.atan(rh - 1.67633) +
                (0.00391838 * Math.pow(rh, 1.5) * Math.atan(0.023101 * rh)) - 4.686035;
    }

    /**
     * 현재 월수 출력
     **/
    private static int getCurrentSeason() {
        return LocalDateTime.now().getMonthValue();
    }
}
