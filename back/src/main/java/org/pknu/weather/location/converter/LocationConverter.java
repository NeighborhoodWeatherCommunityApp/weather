package org.pknu.weather.location.converter;

import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.dto.LocationDTO;

@Slf4j
public class LocationConverter {

    public static Location toLocation(LocationDTO locationDTO) {
        return Location.builder()
                .longitude(locationDTO.getLongitude())
                .latitude(locationDTO.getLatitude())
                .province(locationDTO.getProvince())
                .city(locationDTO.getCity())
                .street(locationDTO.getStreet())
                .build();
    }

    public static LocationDTO toLocationDTO(Location location) {
        return LocationDTO.builder()
                .id(location.getId())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .province(location.getProvince())
                .city(location.getCity())
                .street(location.getStreet())
                .build();
    }
}
