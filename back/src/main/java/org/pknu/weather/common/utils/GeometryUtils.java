package org.pknu.weather.common.utils;


import org.pknu.weather.weather.feignclient.dto.PointDTO;

public final class GeometryUtils {

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
    private static final int WGS84_SRID = 4326; // 기준점 Y좌표

    /**
     * 위경도를 격자(nx, ny) 좌표로 변환을 해주는 메소드 위경도 -> 격자
     *
     * @param lon 경도(degree)
     * @param lat 위도(degree)
     * @return 격자 좌표 x, y 를 가진 Point 객체
     */
    public static PointDTO coordinateToPoint(double lon, double lat) {
        double[] result = lamcProj(lon, lat, 0, 0, 0);
        int x = (int) (result[0] + 1.5);
        int y = (int) (result[1] + 1.5);
        return new PointDTO(x, y);
    }

    /**
     * xy좌표를 위경도로 변환
     */
    public static double[] pointToCoordinate(double x, double y) {
        x -= 1;
        y -= 1;
        return lamcProj(0, 0, x, y, 1);
    }

    /**
     * @param latitude  위도 (-90 ~ 90)
     * @param longitude 경도
     * @return org.locationtech.jts.geom.Point
     */
//    public static Point getPoint(double latitude, double longitude) {
//        GeometryFactory gf = new GeometryFactory();
//        Point point = gf.createPoint(new Coordinate(longitude, latitude));
//        point.setSRID(WGS84_SRID);
//        return point;
//    }
//    private static Geometry createPoint(double latitude, double longitude) throws ParseException {
//        return new WKTReader().read("POINT(" + longitude + " " + latitude + ")");
//    }

//    public static Geometry getMBR(Geometry startGeometry, double distance) {
//        distance /= 111;        // 3km 입력시 111로 나눠야함. buffer의 distance는 단위가 '도' 이기 때문이다.
//        return startGeometry.buffer(distance);
//    }
    public static double[] getSouthwestCoordinate(double latitude, double longitude, double distance) {
        return calculate(latitude, longitude, distance, 225.0);
    }

    public static double[] getNortheastCoordinate(double latitude, double longitude, double distance) {
        return calculate(latitude, longitude, distance, 45.0);
    }

    private static double[] calculate(Double baseLatitude, Double baseLongitude, Double distance,
                                      Double bearing) {
        Double radianLatitude = toRadian(baseLatitude);
        Double radianLongitude = toRadian(baseLongitude);
        Double radianAngle = toRadian(bearing);
        Double distanceRadius = distance / 6371.01;

        Double latitude = Math.asin(sin(radianLatitude) * cos(distanceRadius) +
                cos(radianLatitude) * sin(distanceRadius) * cos(radianAngle));
        Double longitude = radianLongitude + Math.atan2(sin(radianAngle) * sin(distanceRadius) *
                cos(radianLatitude), cos(distanceRadius) - sin(radianLatitude) * sin(latitude));

        longitude = normalizeLongitude(longitude);
        return new double[]{toDegree(latitude), toDegree(longitude)};
    }

    private static Double toRadian(Double coordinate) {
        return coordinate * Math.PI / 180.0;
    }

    private static Double toDegree(Double coordinate) {
        return coordinate * 180.0 / Math.PI;
    }

    private static Double sin(Double coordinate) {
        return Math.sin(coordinate);
    }

    private static Double cos(Double coordinate) {
        return Math.cos(coordinate);
    }

    private static Double normalizeLongitude(Double longitude) {
        return (longitude + 540) % 360 - 180;
    }

    /**
     * Lambert Conformal Conic Projection 수행.
     *
     * @param lon 경도
     * @param lat 위도
     * @return 변환 방향에 따른 (X, Y) 좌표
     */
    private static double[] lamcProj(double lon, double lat, double x, double y, int code) {
        double PI = Math.asin(1.0) * 2.0;
        double DEGRAD = PI / 180.0;
        double RADDEG = 180.0 / PI;
        double re, olon, olat, sn, sf, ro;

        re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SALT2 * DEGRAD;
        olon = OION * DEGRAD;
        olat = OLAT * DEGRAD;

        sn = Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        sf = Math.tan(PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        ro = Math.tan(PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra, theta;
        if (code == 0) {
            ra = Math.tan(PI * 0.25 + lat * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            theta = lon * DEGRAD - olon;

            if (theta > PI) {
                theta -= 2.0 * PI;
            }
            if (theta < -PI) {
                theta += 2.0 * PI;
            }

            theta *= sn;
            x = ra * Math.sin(theta) + XO;
            y = ro - ra * Math.cos(theta) + YO;

            return new double[]{x, y};
        } else {
            double xn = x - XO;
            double yn = ro - y + YO;

            ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            lat = Math.pow((re * sf / ra), (1.0 / sn));
            lat = 2.0 * Math.atan(lat) - PI * 0.5;

            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                } else {
                    theta = Math.atan2(xn, yn);
                }
            }

            lon = theta / sn + olon;
            return new double[]{lon * RADDEG, lat * RADDEG};
        }
    }
}