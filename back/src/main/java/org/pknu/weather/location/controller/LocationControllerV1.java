package org.pknu.weather.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.location.dto.LocationDTO;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.location.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationControllerV1 {
    private final LocationService locationService;

    @PostMapping("/coor")
    public ApiResponse<LocationDTO> saveMemberLocation( @RequestHeader("Authorization") String authorization,
                                                        @RequestBody Map<String, Object> payload) {
        log.debug("/api/v1/location/coor controller start ............");

        String email = getEmailByToken(authorization);

        double longitude = Double.parseDouble(String.valueOf(payload.get("longitude")));
        double latitude = Double.parseDouble(String.valueOf(payload.get("latitude")));

        LocationDTO savedLocation = locationService.saveLocation(email,longitude, latitude);

        return ApiResponse.onSuccess(savedLocation);
    }

    @GetMapping("/defaultLoc")
    public ApiResponse<LocationDTO> getMemberDefaultLocation(@RequestHeader("Authorization") String authorization) {

        log.debug("/api/v1/location/defaultLoc controller start ............");

        String email = getEmailByToken(authorization);

        LocationDTO savedLocation = locationService.findDefaultLocation(email);

        return ApiResponse.onSuccess(savedLocation);
    }

    @GetMapping("/locationInfo")
    public ApiResponse<List<String>> getLocationName(@RequestParam(required = false) String province, String city) {

        if((city != null && !city.isEmpty()) && (province == null || province.isEmpty()))
            throw new GeneralException(ErrorStatus._PROVINCE_NOT_FOUND);

        List<String> locationInfo = locationService.getLocation(province, city);

        return ApiResponse.onSuccess(locationInfo);
    }

    @PostMapping("/locationInfo")
    public ApiResponse<LocationDTO> createLocation(@RequestBody LocationDTO locationDTO) {

        checkLocationInfo(locationDTO);

        LocationDTO savedLocationDTO = locationService.saveLocation(locationDTO);

        return ApiResponse.onSuccess(savedLocationDTO);
    }

    @GetMapping("/defaultLocation")
    public void setDefaultLocation() {

        locationService.setDefaultLocation();

    }

    private void checkLocationInfo(LocationDTO locationDTO) {
        if(locationDTO.getProvince() == null || locationDTO.getCity() == null || locationDTO.getStreet() == null) {
            throw new GeneralException(ErrorStatus._MALFORMED_ADDRESS_INFORMATION);
        }
        if(locationDTO.getProvince().isEmpty() || locationDTO.getCity().isEmpty() || locationDTO.getStreet().isEmpty()) {
            throw new GeneralException(ErrorStatus._MALFORMED_ADDRESS_INFORMATION);
        }
    }
}