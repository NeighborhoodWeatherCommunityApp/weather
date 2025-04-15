package org.pknu.weather.controller;


import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.dto.ExpEventRequestDto;
import org.pknu.weather.dto.ExpEventResponseDto;
import org.pknu.weather.dto.converter.ExpEventResponseConverter;
import org.pknu.weather.service.ExpRewardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ExpRewardControllerV1 {
    private final ExpRewardService expRewardService;

    @PostMapping("/members/me/exp")
    public ApiResponse<Object> rewardExp(@RequestHeader("Authorization") String authorization,
                                         @Valid @RequestBody ExpEventRequestDto expEventRequestDto) {
        String email = getEmailByToken(authorization);
        expRewardService.rewardExp(email, expEventRequestDto.getExpEvent());
        return ApiResponse.onSuccess();
    }

    @GetMapping("/exp/events/available")
    public ApiResponse<List<ExpEventResponseDto>> getExpEventAvailableList(
            @RequestHeader("Authorization") String authorization) {
        List<ExpEventResponseDto> expEventResponseDtoList = Arrays.stream(ExpEvent.values())
                .filter(ExpEvent::getAllowApiRequest)
                .map(ExpEventResponseConverter::toExpResponseDto)
                .toList();

        return ApiResponse.onSuccess(expEventResponseDtoList);
    }

    @GetMapping("/exp/events/all")
    public ApiResponse<List<ExpEventResponseDto>> getExpEventList(
            @RequestHeader("Authorization") String authorization) {

        List<ExpEventResponseDto> expEventResponseDtoList = Arrays.stream(ExpEvent.values())
                .filter(ExpEvent::getAllowApiRequest)
                .map(ExpEventResponseConverter::toExpResponseDto)
                .toList();

        return ApiResponse.onSuccess(expEventResponseDtoList);
    }
}
