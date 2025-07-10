package org.pknu.weather.feignClient;

import feign.Response;
import org.pknu.weather.security.dto.AppleAuthTokenResponse;
import org.pknu.weather.security.dto.ApplePublicKeyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

@FeignClient(name = "appleAuthClient", url = "${apple.public-key-url}")
public interface AppleAuthClient {
    @GetMapping("/auth/keys")
    ApplePublicKeyResponse getAppleAuthPublicKey();

    @PostMapping(value = "/auth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AppleAuthTokenResponse getAccessToken(
            @RequestPart("code") String code,
            @RequestPart("client_id") String client_id,
            @RequestPart("client_secret") String client_secret,
            @RequestPart("grant_type") String grant_type
    );

    @PostMapping(value = "/auth/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Response revokeToken(
            @RequestPart("client_id") String clientId,
            @RequestPart("client_secret") String clientSecret,
            @RequestPart("token") String token
    );
}