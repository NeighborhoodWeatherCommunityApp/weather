package org.pknu.weather.member.auth.userInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.auth.feignClient.AppleAuthClient;
import org.pknu.weather.member.auth.generator.ApplePublicKeyGenerator;
import org.pknu.weather.security.util.JWTUtil;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleUserInfoProvider implements UserInfoProvider {
    private final JWTUtil jwtUtil;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleAuthClient appleAuthClient;
    @Override
    public SocialUserInfo getUserInfo(String identityToken) {
        log.debug("AppleUserInfoProvider start.................");

        Map<String, String> parsedHeader = extractJwtHeader(identityToken);

        PublicKey applePublicKey = getPublicKey(parsedHeader);

        Claims tokenClaims = jwtUtil.getTokenClaims(identityToken, applePublicKey);

        return SocialUserInfo.builder()
                .type("apple")
                .email(String.valueOf(tokenClaims.get("email")))
                .build();
    }

    private PublicKey getPublicKey(Map<String, String> jwtHeader) {
        try {
            return applePublicKeyGenerator.generatePublicKey(jwtHeader, appleAuthClient.getAppleAuthPublicKey());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("애플 공개키 생성 실패: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> extractJwtHeader(String identityToken) {
        try {
            return jwtUtil.parseHeaders(identityToken);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("apple identity token 헤더 추출 실패: " + jsonProcessingException.getMessage(), jsonProcessingException);
            throw new GeneralException(ErrorStatus.MALFORMED_APPLE_TOKEN);
        }
    }
}
