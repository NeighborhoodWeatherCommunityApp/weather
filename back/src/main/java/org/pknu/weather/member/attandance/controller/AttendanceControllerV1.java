package org.pknu.weather.member.attandance.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apipayload.ApiResponse;
import org.pknu.weather.member.attandance.service.AttendanceCacheService;
import org.pknu.weather.member.attandance.service.AttendanceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance")
public class AttendanceControllerV1 {

    private final AttendanceService attendanceService;
    private final AttendanceCacheService attendanceCacheService;

    @PostMapping("/check-in")
    public ApiResponse<Boolean> checkIn(@RequestHeader("Authorization") String authorization) {
        boolean result = attendanceCacheService.checkIn(getEmailByToken(authorization), LocalDate.now());
        return ApiResponse.onSuccess();
    }
}
