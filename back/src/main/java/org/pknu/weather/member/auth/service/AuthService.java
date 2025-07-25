package org.pknu.weather.member.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.auth.userInfo.SocialUserInfo;
import org.pknu.weather.security.exception.TokenException;
import org.pknu.weather.member.auth.generator.AppTokenGenerator;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.member.auth.userInfo.AppleUserInfoProvider;
import org.pknu.weather.member.auth.userInfo.KakaoUserInfoProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;
    private final AppTokenGenerator appTokenGenerator;
    private final KakaoUserInfoProvider kakaoUserInfoStrategy;
    private final AppleUserInfoProvider appleUserInfoStrategy;
    private static final int ACCESS_TOKEN_VALID_DAYS = 3;
    private static final int REFRESH_TOKEN_VALID_DAYS = 30;


    public Map<String, String> generateTokens(String tokenType, String accessToken) {

        SocialUserInfo userInfo = null;

        if (tokenType.equals("kakao"))
            userInfo = appTokenGenerator.getUserInfo(kakaoUserInfoStrategy, accessToken);
        else if (tokenType.equals("apple"))
            userInfo = appTokenGenerator.getUserInfo(appleUserInfoStrategy, accessToken);
        else {
            log.error("social 로그인의 타입에 문제가 발생했습니다.");
            throw new GeneralException(ErrorStatus.TYPE_NOT_ACCEPTED);
        }

        return appTokenGenerator.generateAppToken(userInfo.getUserInfo());
    }

    public Map<String, String> refreshTokens(String refreshToken) throws TokenException {

        Map<String, Object> refreshClaims = checkRefreshToken(refreshToken);
        Long expiredDate = ((Number) refreshClaims.get("exp")).longValue();
        getNewClaims(refreshClaims);

        String renewedAccessToken = jwtUtil.generateToken(refreshClaims, ACCESS_TOKEN_VALID_DAYS);
        String renewedRefreshToken = refreshToken;

        if (isRefreshTokenRenewNeed(expiredDate)) {
            log.info("new Refresh Token required.................");
            renewedRefreshToken = jwtUtil.generateToken(refreshClaims, REFRESH_TOKEN_VALID_DAYS);
        }

        return Map.of(
                "accessToken", renewedAccessToken,
                "refreshToken", renewedRefreshToken
        );
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) throws TokenException {

        try {
            return jwtUtil.validateToken(refreshToken);
        }catch(ExpiredJwtException expiredJwtException){
            throw new TokenException(ErrorStatus.EXPIRED_REFRESH_TOKEN);
        }catch(Exception exception){
            throw new TokenException(ErrorStatus.MALFORMED_REFRESH_TOKEN);
        }
    }

    private boolean isRefreshTokenRenewNeed(Long exp) {
        long nowSeconds = Instant.now().getEpochSecond();
        long threeDaysInSeconds = 60 * 60 * 24 * 3;

        return (exp - nowSeconds) < threeDaysInSeconds;
    }

    private void getNewClaims(Map<String, Object> refreshClaims) {
        refreshClaims.remove("iat");
        refreshClaims.remove("exp");
    }


}
