package org.pknu.weather.member.auth.feignClient;


import feign.Response;
import org.pknu.weather.member.auth.dto.KakaoUserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;

@Component
@FeignClient(value = "login", url = "https://kapi.kakao.com")
public interface KaKaoAuthClient {

    @GetMapping("/v1/user/access_token_info")
    Response checkKakaoAccessToken(@RequestHeader("Authorization") String accessToken);

    @GetMapping("/v2/user/me")
    KakaoUserResponseDTO getMemberData(@RequestHeader("Authorization") String accessToken,
                                       @RequestHeader("Content-type") String contentType);

    @PostMapping(value = "/v1/user/logout", consumes = "application/x-www-form-urlencoded")
    Response deleteMemberData(@RequestHeader("Authorization") String kakaoAdminKey,
                              @RequestPart(value = "target_id_type") String targetIdType,
                              @RequestPart(value = "target_id") Long targetId);

}
