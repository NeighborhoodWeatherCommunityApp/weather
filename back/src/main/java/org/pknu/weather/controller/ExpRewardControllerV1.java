package org.pknu.weather.controller;


import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.dto.ExpEventRequestDto;
import org.pknu.weather.event.ExpRewardEvent;
import org.pknu.weather.service.ExpRewardService;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/members/me/exp")
    public ApiResponse<Object> rewardExp(@RequestHeader("Authorization") String authorization,
                                         @Valid @RequestBody ExpEventRequestDto expEventRequestDto) {
        String email = getEmailByToken(authorization);
        eventPublisher.publishEvent(new ExpRewardEvent(email, expEventRequestDto.getExpEvent()));
        return ApiResponse.onSuccess();
    }

    @PostMapping("/epx/rewards/share-kakao")
    public ApiResponse<Object> shareKakaoEvent(@RequestHeader("Authorization") String authorization) {
        String email = getEmailByToken(authorization);
        eventPublisher.publishEvent(new ExpRewardEvent(email, ExpEvent.SHARE_KAKAO));
        return ApiResponse.onSuccess();
    }
}
