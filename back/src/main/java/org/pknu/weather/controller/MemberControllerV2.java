package org.pknu.weather.controller;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.MemberResponse;
import org.pknu.weather.dto.MemberResponse.MemberResponseWithAddressDTO;
import org.pknu.weather.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/member")
public class MemberControllerV2 {

    private final MemberService memberService;

    @GetMapping(value = "/info")
    public ApiResponse<MemberResponseWithAddressDTO> getMemberInfo(
            @RequestHeader("Authorization") String authorization) {
        String email = getEmailByToken(authorization);
        MemberResponse.MemberResponseWithAddressDTO fullMemberInfo = memberService.findFullMemberInfoByEmail(email);
        return ApiResponse.onSuccess(fullMemberInfo);
    }
}
