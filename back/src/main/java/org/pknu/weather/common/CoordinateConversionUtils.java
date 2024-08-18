package org.pknu.weather.common;

import org.pknu.weather.dto.Point;

public final class CoordinateConversionUtils {

    private static final int NX = 149; // X 축 격자점 수
    private static final int ny = 253; // Y 축 격자점 수
    private static final float RE = 6371.00877f; // 지도반경
    private static final float GRID = 5.0f; // 격자간격 (km)
    private static final float SLAT1 = 30.0f; // 표준위도 1
    private static final float SALT2 = 60.0f; // 표준위도 2
    private static final float OION = 126.0f; // 기준점 경도
    private static final float OLAT = 38.0f; // 기준점 위도
    private static final float XO = 210 / GRID; // 기준점 X좌표
    private static final float YO = 675 / GRID; // 기준점 Y좌표

    /**
     * 위경도를 격자(nx, ny) 좌표로 변환을 해주는 메소드
     * 위경도 -> 격자
     *
     * @param lon 경도(degree)
     * @param lat 위도(degree)
     * @return 격자 좌표 x, y 를 가진 Point 객체
     */
    public static Point convertCoordinate(float lon, float lat) {
        float[] result = lamcproj(lon, lat);
        int x = (int) (result[0] + 1.5);
        int y = (int) (result[1] + 1.5);
        return new Point(x, y);
    }

    /**
     * Lambert Conformal Conic Projection 수행.
     *
     * @param lon  경도
     * @param lat  위도
     * @return 변환 방향에 따른 (X, Y) 좌표
     */
    private static float[] lamcproj(float lon, float lat) {
        double PI = Math.PI;
        double DEGRAD = PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SALT2 * DEGRAD;
        double olon = OION * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra, theta;

        ra = Math.tan(PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        theta = lon * DEGRAD - olon;
        if (theta > PI) theta -= 2.0 * PI;
        if (theta < -PI) theta += 2.0 * PI;
        theta *= sn;
        double x = ra * Math.sin(theta) + XO;
        double y = ro - ra * Math.cos(theta) + YO;
        return new float[]{(float) x, (float) y};
    }
}