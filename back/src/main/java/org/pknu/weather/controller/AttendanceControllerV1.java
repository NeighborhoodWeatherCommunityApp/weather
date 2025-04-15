package org.pknu.weather.controller;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.service.AttendanceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance")
public class AttendanceControllerV1 {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    public ApiResponse<Boolean> checkIn(@RequestHeader("Authorization") String authorization) {
        attendanceService.checkIn(getEmailByToken(authorization));
        return ApiResponse.onSuccess();
    }
}
