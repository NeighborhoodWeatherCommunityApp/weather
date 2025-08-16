package org.pknu.weather.alarm.controller;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.alarm.enums.AlarmType;
import org.pknu.weather.alarm.dto.AlarmRequestDTO;
import org.pknu.weather.alarm.dto.AlarmResponseDTO;
import org.pknu.weather.alarm.service.AlarmService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@Validated
public class AlarmControllerV2 {

    private final AlarmService alarmService;

    @PostMapping("/alarm")
    public ApiResponse<Object> createAlarm(@RequestHeader("Authorization") String authorization,
                                                 @Valid @RequestBody AlarmRequestDTO alarmRequestDTO) {
        String email = TokenConverter.getEmailByToken(authorization);
        alarmService.saveAlarm(email, alarmRequestDTO);
        return ApiResponse.onSuccess();
    }

    @PatchMapping("/alarm")
    public ApiResponse<Object> patchAlarm(@RequestHeader("Authorization") String authorization,
                                          @Valid @RequestBody AlarmRequestDTO alarmRequestDTO) {
        String email = TokenConverter.getEmailByToken(authorization);
        alarmService.modifyAlarm(email, alarmRequestDTO);
        return ApiResponse.onSuccess();
    }

    @PostMapping("/alarm/by-fcm")
    public ApiResponse<Object> getAlarm(@RequestHeader("Authorization") String authorization,
                                        @RequestBody Map<String, String> payload) {

        String email = TokenConverter.getEmailByToken(authorization);
        String fcmToken = payload.get("fcmToken");
        AlarmResponseDTO foundAlarm = alarmService.getAlarm(email, fcmToken);
        return ApiResponse.onSuccess(foundAlarm);
    }

    @PostMapping("/testAlarm")
    public ApiResponse<Object> testAlarm(@RequestHeader("Authorization") String authorization,
                                         @RequestBody Map<String, String> payload) {

        String email = TokenConverter.getEmailByToken(authorization);
        Map<String, String> testArgs = Map.of("email", email, "fcmToken", payload.get("fcmToken"));

        alarmService.trigger(AlarmType.TEST_WEATHER_SUMMARY, testArgs);
        return ApiResponse.onSuccess();
    }
}
