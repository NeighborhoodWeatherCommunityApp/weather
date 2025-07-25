package org.pknu.weather.member.unitTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.ExpiredJwtException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.auth.generator.AppTokenGenerator;
import org.pknu.weather.member.auth.service.AuthService;
import org.pknu.weather.member.auth.userInfo.AppleUserInfoProvider;
import org.pknu.weather.member.auth.userInfo.KakaoUserInfo;
import org.pknu.weather.member.auth.userInfo.KakaoUserInfoProvider;
import org.pknu.weather.member.auth.userInfo.SocialUserInfo;
import org.pknu.weather.security.exception.TokenException;
import org.pknu.weather.security.util.JWTUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private AppTokenGenerator appTokenGenerator;

    @Mock
    private KakaoUserInfoProvider kakaoUserInfoStrategy;

    @Mock
    private AppleUserInfoProvider appleUserInfoStrategy;

    @InjectMocks
    private AuthService authService;

    private SocialUserInfo mockSocialUserInfo;
    private final Map<String, String> mockAppTokens= Map.of(
            "accessToken", "mockAccessToken",
            "refreshToken", "mockRefreshToken",
            "isNewMember", "true");

    private final int ACCESS_TOKEN_VALID_DAYS = 3;
    private final int REFRESH_TOKEN_VALID_DAYS = 30;

    @Test
    void 카카오_로그인을_통한_앱_토큰_생성() {
        // Given
        String tokenType = "kakao";
        String email = "test@example.com";
        long kakaoId = 1L;
        String kakaoAccessToken = "kakaoAccessToken";

        Map<String, Object>  mockUserInfoMap = new HashMap<>();
        mockUserInfoMap.put("type",tokenType);
        mockUserInfoMap.put("email", email);
        mockUserInfoMap.put("kakaoId", String.valueOf(kakaoId));

        mockSocialUserInfo = new KakaoUserInfo(tokenType, email, kakaoId);

        when(appTokenGenerator.getUserInfo(kakaoUserInfoStrategy, kakaoAccessToken))
                .thenReturn(mockSocialUserInfo);
        when(appTokenGenerator.generateAppToken(mockUserInfoMap))
                .thenReturn(mockAppTokens);

        // When
        Map<String, String> result = authService.generateTokens(tokenType, kakaoAccessToken);

        // Then
        assertThat(result)
                .isNotNull()
                .containsEntry("accessToken", "mockAccessToken")
                .containsEntry("refreshToken", "mockRefreshToken");
    }

    @Test
    void 애플_로그인을_통한_앱_토큰_생성() {
        // Given
        String tokenType = "apple";
        String email = "test@example.com";
        String appleAccessToken = "appleAccessToken";

        Map<String, Object>  mockUserInfoMap = new HashMap<>();
        mockUserInfoMap.put("type",tokenType);
        mockUserInfoMap.put("email", email);

        mockSocialUserInfo = new SocialUserInfo(tokenType, email);

        when(appTokenGenerator.getUserInfo(appleUserInfoStrategy, appleAccessToken))
                .thenReturn(mockSocialUserInfo);
        when(appTokenGenerator.generateAppToken(mockUserInfoMap))
                .thenReturn(mockAppTokens);

        // When
        Map<String, String> result = authService.generateTokens(tokenType, appleAccessToken);

        // Then
        assertThat(result)
                .isNotNull()
                .containsEntry("accessToken", "mockAccessToken")
                .containsEntry("refreshToken", "mockRefreshToken");
    }

    @Test
    void 지원하지_않은_소셜_로그인_타입을_통한_앱_토큰_생성_실패() {
        // Given
        String tokenType = "google";
        String email = "test@example.com";
        String googleAccessToken = "googleAccessToken";

        mockSocialUserInfo = new SocialUserInfo(tokenType, email);

        // When & Then
        assertThatThrownBy(() -> authService.generateTokens(tokenType, googleAccessToken))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(ErrorStatus.TYPE_NOT_ACCEPTED);
    }

    @Test
    void 리프레시_토큰을_통한_어세스_토큰_갱신() throws TokenException {
        // Given
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("exp", Instant.now().getEpochSecond() + (60 * 60 * 24 * 5)); // 5일 후 만료

        when(jwtUtil.validateToken(refreshToken)).thenReturn(claims);
        when(jwtUtil.generateToken(any(Map.class), eq(ACCESS_TOKEN_VALID_DAYS)))
                .thenReturn("renewedAccessTokenValue");

        // When
        Map<String, String> result = authService.refreshTokens(refreshToken);

        // Then
        assertThat(result)
                .isNotNull()
                .containsEntry("accessToken", "renewedAccessTokenValue")
                .containsEntry("refreshToken", refreshToken);
    }

    @Test
    void 리프레시_토큰_만기일이_얼마_남지않은_경우_모든_토큰_갱신() throws TokenException {
        // Given
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("exp", Instant.now().getEpochSecond() + (60 * 60 * 24 * 1)); // 1일 후 만료

        when(jwtUtil.validateToken(refreshToken)).thenReturn(claims);
        when(jwtUtil.generateToken(any(Map.class), eq(ACCESS_TOKEN_VALID_DAYS)))
                .thenReturn("renewedAccessTokenValue");
        when(jwtUtil.generateToken(any(Map.class), eq(REFRESH_TOKEN_VALID_DAYS)))
                .thenReturn("newRefreshTokenValue");

        // When
        Map<String, String> result = authService.refreshTokens(refreshToken);

        // Then
        assertThat(result)
                .isNotNull()
                .containsEntry("accessToken", "renewedAccessTokenValue")
                .containsEntry("refreshToken", "newRefreshTokenValue"); // 새로운 리프레시 토큰이 반환되는지 확인
    }

    @Test
    void 만료된_리프레시_토큰으로_갱신_시_실패() {
        // Given
        String expiredRefreshToken = "expiredRefreshToken";
        when(jwtUtil.validateToken(expiredRefreshToken))
                .thenThrow(new ExpiredJwtException(null, null, "JWT expired"));

        // When & Then
        assertThatThrownBy(() -> authService.refreshTokens(expiredRefreshToken))
                .isInstanceOf(TokenException.class)
                .extracting("code")
                .isEqualTo(ErrorStatus.EXPIRED_REFRESH_TOKEN);
    }

    @DisplayName("유효하지 않은 리프레시 토큰으로 갱신 시 TokenException (MALFORMED_REFRESH_TOKEN) 발생 - AssertJ")
    @Test
    void 유효하지_않은_리프레시_토큰으로_갱신_시_실패() {
        // Given
        String malformedRefreshToken = "malformedRefreshToken";
        when(jwtUtil.validateToken(malformedRefreshToken))
                .thenThrow(new RuntimeException("Invalid JWT signature"));

        // When & Then
        assertThatThrownBy(() -> authService.refreshTokens(malformedRefreshToken))
                .isInstanceOf(TokenException.class)
                .extracting("code")
                .isEqualTo(ErrorStatus.MALFORMED_REFRESH_TOKEN);
    }

    @Test
    void jwtUtil에서_예외_발생_시_실패() {
        // Given
        String refreshToken = "someRefreshToken";
        when(jwtUtil.validateToken(refreshToken))
                .thenThrow(new IllegalArgumentException("Some other JWT related error"));

        // When & Then
        assertThatThrownBy(() -> authService.refreshTokens(refreshToken))
                .isInstanceOf(TokenException.class)
                .extracting("code")
                .isEqualTo(ErrorStatus.MALFORMED_REFRESH_TOKEN);
    }
}