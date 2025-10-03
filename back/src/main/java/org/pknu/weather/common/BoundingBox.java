package org.pknu.weather.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.location.entity.Location;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BoundingBox {
    private double leftLat;
    private double rightLat;
    private double leftLon;
    private double rightLon;

    public static BoundingBox calculateBoundingBox(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double distanceInKm = GlobalParams.RADIUS_DISTANCE_KM;

        final double EARTH_RADIUS = 6371.0; // 지구 반지름 (단위: km)

        // 위도 1도는 약 111km
        double latDelta = distanceInKm / 111.0;

        // 경도 1도는 cos(latitude) * 111km
        double lonDelta = distanceInKm / (Math.cos(Math.toRadians(latitude)) * 111.0);

        double minLat = latitude - latDelta;
        double maxLat = latitude + latDelta;
        double minLon = longitude - lonDelta;
        double maxLon = longitude + lonDelta;

        return new BoundingBox(minLat, maxLat, minLon, maxLon);
    }
}


