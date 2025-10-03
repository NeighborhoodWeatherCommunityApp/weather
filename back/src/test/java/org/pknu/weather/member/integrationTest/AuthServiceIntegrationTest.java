package org.pknu.weather.member.integrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import feign.Request;
import feign.Response;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.auth.dto.AppleJwtKeyDTO;
import org.pknu.weather.member.auth.dto.AppleJwtKeysResponseDTO;
import org.pknu.weather.member.auth.dto.KakaoUserResponseDTO;
import org.pknu.weather.member.auth.feignClient.AppleAuthClient;
import org.pknu.weather.member.auth.feignClient.KaKaoAuthClient;
import org.pknu.weather.member.auth.service.AuthService;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.service.MemberService;
import org.pknu.weather.security.exception.TokenException;
import org.pknu.weather.security.jwt.JWTUtil;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private KaKaoAuthClient kaKaoAuthClient;

    @MockBean
    private AppleAuthClient appleAuthClient;

    @MockBean
    private MemberService memberService;

    private static final String EXCEPTION_FIELD = "code";

    @BeforeEach
    void setUp() {
        when(memberService.findMemberByEmail(any(String.class))).thenReturn(Optional.empty());
        when(memberService.saveMember(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void 카카오_로그인_성공_시_토큰_생성() {
        // Given
        String kakaoAccessToken = "mockKakaoAccessToken";
        String testUserEmail = "kakao_new_member@example.com";
        Long kakaoUserId = 12345L;
        String authHeader = "Bearer " + kakaoAccessToken;

        // KaKaoAuthClient Mocking
        Response mockCheckTokenResponse = Response.builder()
                .request(Request.create(Request.HttpMethod.GET, "/v1/user/access_token_info", Collections.emptyMap(), null, StandardCharsets.UTF_8, null))
                .status(HttpStatus.OK.value())
                .headers(Collections.emptyMap())
                .build();

        when(kaKaoAuthClient.checkKakaoAccessToken(authHeader)).thenReturn(mockCheckTokenResponse);

        KakaoUserResponseDTO kakaoUserResponseDTO = new KakaoUserResponseDTO(
                kakaoUserId,
                LocalDateTime.now(),
                new KakaoUserResponseDTO.KakaoAccount(testUserEmail));

        when(kaKaoAuthClient.getMemberData(authHeader, "application/x-www-form-urlencoded;charset=utf-8"))
                .thenReturn(kakaoUserResponseDTO);

        when(jwtUtil.generateToken(any(Map.class), eq(3))).thenReturn("mockAppAccessToken");
        when(jwtUtil.generateToken(any(Map.class), eq(30))).thenReturn("mockAppRefreshToken");

        // When
        Map<String, String> result = authService.generateTokens("kakao", kakaoAccessToken);

        // Then
        assertThat(result)
                .isNotNull()
                .containsEntry("accessToken", "mockAppAccessToken")
                .containsEntry("refreshToken", "mockAppRefreshToken")
                .containsEntry("isNewMember", "true");
    }

    @Test
    void 애플_로그인_성공_시_토큰_생성() throws JsonProcessingException {

        // given
        final String appleIdentityToken = "mockAppleIdentityToken";
        final String testUserEmail = "apple_new_member@example.com";
        final String expectedAccessToken = "mockAppleAppAccessToken";
        final String expectedRefreshToken = "mockAppleAppRefreshToken";
        final String expectedIsNewMember = "true";
        final String mockKid = "ABCDEF1234";

        AppleJwtKeysResponseDTO mockAppleKeysResponse = getAppleJwtKeysResponseDTO(mockKid);

        when(appleAuthClient.getAppleAuthPublicKey()).thenReturn(mockAppleKeysResponse);

        Map<String, String> mockHeader = Map.of("alg", "RS256", "kid", mockKid);
        when(jwtUtil.parseHeaders(anyString())).thenReturn(mockHeader);

        Claims mockClaims = Jwts.claims();
        mockClaims.setSubject("apple_user_sub_id");
        mockClaims.put("email", testUserEmail);
        when(jwtUtil.getTokenClaims(any(), any())).thenReturn(mockClaims);

        when(jwtUtil.generateToken(any(Map.class), eq(3))).thenReturn(expectedAccessToken);
        when(jwtUtil.generateToken(any(Map.class), eq(30))).thenReturn(expectedRefreshToken);

        // when
        Map<String, String> result = authService.generateTokens("apple", appleIdentityToken);

        // then
        assertThat(result)
                .isNotNull()
                .containsEntry("accessToken", expectedAccessToken)
                .containsEntry("refreshToken", expectedRefreshToken)
                .containsEntry("isNewMember", expectedIsNewMember);
    }

    private AppleJwtKeysResponseDTO getAppleJwtKeysResponseDTO(String mockKid) {
        List<AppleJwtKeyDTO> test = new ArrayList<>();
        test.add(createMockAppleKey(mockKid));
        test.add(createMockAppleKey("testKid1"));
        test.add(createMockAppleKey("testKid2"));

        return new AppleJwtKeysResponseDTO(test);
    }

    private AppleJwtKeyDTO createMockAppleKey(String kid) {
        String validN = Base64.getUrlEncoder().encodeToString(
                "this_is_a_sufficiently_long_and_valid_n_value_for_apple_jwt_key_mocking_purposes".getBytes(StandardCharsets.UTF_8)
        );
        String validE = "AQAB"; // Base64URL for 65537

        return new AppleJwtKeyDTO("RSA", kid, "RS256", validN, validE);
    }

    @Test
    void 리프레시_토큰의_유효기간이_충분할_경우_액세스_토큰만_갱신() throws Exception {

        // Given
        String refreshToken = "validRefreshToken";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "existing_user@example.com");
        claims.put("exp", Instant.now().getEpochSecond() + (60 * 60 * 24 * 5)); // 5일 후 만료 (갱신 조건: 3일 미만)

        when(jwtUtil.validateToken(refreshToken)).thenReturn(claims);
        when(jwtUtil.generateToken(any(Map.class), eq(3))).thenReturn("renewedAccessToken");

        // When
        Map<String, String> result = authService.refreshTokens(refreshToken);

        // Then
        assertThat(result)
                .isNotNull()
                .containsEntry("accessToken", "renewedAccessToken")
                .containsEntry("refreshToken", refreshToken);
    }

    @Test
    void 리프레시_토큰_갱신이_필요할_경우_액세스_리프레시_토큰_모두_갱신() throws TokenException {
        // Given
        String refreshToken = "oldRefreshToken";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "user@example.com");
        claims.put("exp", Instant.now().getEpochSecond() + (60 * 60 * 24 * 1)); // 1일 후 만료 (갱신 조건: 3일 미만)

        when(jwtUtil.validateToken(refreshToken)).thenReturn(claims);
        when(jwtUtil.generateToken(any(Map.class), eq(3))).thenReturn("renewedAccessToken");
        when(jwtUtil.generateToken(any(Map.class), eq(30))).thenReturn("newRefreshToken");

        // When
        Map<String, String> result = authService.refreshTokens(refreshToken);

        // Then
        assertThat(result)
                .isNotNull()
                .containsEntry("accessToken", "renewedAccessToken")
                .containsEntry("refreshToken", "newRefreshToken");
    }

    @Test
    void 만료된_리프레시_토큰으로_갱신_시_EXPIRED_REFRESH_TOKEN_예외_발생() {
        // Given
        String expiredRefreshToken = "expiredRefreshToken";
        when(jwtUtil.validateToken(expiredRefreshToken)).thenThrow(ExpiredJwtException.class);

        // When & Then
        assertThatThrownBy(() -> authService.refreshTokens(expiredRefreshToken))
                .isInstanceOf(TokenException.class)
                .extracting(EXCEPTION_FIELD)
                .isEqualTo(ErrorStatus.EXPIRED_REFRESH_TOKEN);
    }

    @Test
    void 유효하지_않은_형식의_리프레시_토큰으로_갱신_시_MALFORMED_REFRESH_TOKEN_예외_발생() {
        // Given
        String malformedRefreshToken = "malformedToken";
        when(jwtUtil.validateToken(malformedRefreshToken)).thenThrow(new MalformedJwtException("Malformed token"));

        // When & Then
        assertThatThrownBy(() -> authService.refreshTokens(malformedRefreshToken))
                .isInstanceOf(TokenException.class)
                .extracting(EXCEPTION_FIELD)
                .isEqualTo(ErrorStatus.MALFORMED_REFRESH_TOKEN);
    }

    @Test
    void 지원하지_않는_소셜로그인_타입_시_TYPE_NOT_ACCEPTED_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> authService.generateTokens("google", "someAccessToken"))
                .isInstanceOf(GeneralException.class)
                .extracting(EXCEPTION_FIELD)
                .isEqualTo(ErrorStatus.TYPE_NOT_ACCEPTED);
    }
}