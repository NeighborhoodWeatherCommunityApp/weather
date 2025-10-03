package org.pknu.weather.member.auth.unlink;

import feign.Response;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.auth.feignClient.AppleAuthClient;
import org.pknu.weather.member.auth.dto.AppleAuthTokenResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Component
@Slf4j
public class AppleUnlinker implements UserUnlinker {

    private final AppleAuthClient appleAuthClient;

    private static final String APP_BUNDLE_ID = "com.capstone.weatherapp";
    private static final String GRANT_TYPE_AUTH_CODE = "authorization_code";
    private static final String APPLE_AUDIENCE = "https://appleid.apple.com";
    
    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key.id}")
    private String keyId;

    @Value("${apple.key.content}")
    private String key;

    @Override
    public void unlinkUser(String userInfo) {

        AppleAuthTokenResponseDTO tokenResponse = generateAuthTokenSafely(userInfo);
        String appleAuthToken = tokenResponse.getAccessToken();

        if (appleAuthToken == null) {
            log.error("애플 access token이 null입니다. 회원 탈퇴 실패");
            return;
        }

        revokeAppleTokenSafely(appleAuthToken);
    }

    private void revokeAppleTokenSafely(String appleAuthToken) {
        try (Response response = appleAuthClient.revokeToken(
                APP_BUNDLE_ID,
                createClientSecret(),
                appleAuthToken
        )) {
            log.info("애플 회원 탈퇴(연결 해제) 완료 - status: {}", response.status());
        } catch (Exception e) {
            log.error("Apple 회원 탈퇴(연결 해제) 처리 중 예외 발생", e);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    private AppleAuthTokenResponseDTO generateAuthTokenSafely(String userInfo) {
        try {
            return generateAuthToken(userInfo);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Apple 토큰 생성 실패", e);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    public String createClientSecret() throws NoSuchAlgorithmException, InvalidKeySpecException {

        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", keyId);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience(APPLE_AUDIENCE)
                .setSubject(clientId)
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String base64PrivateKey = key;

        byte[] encoded = Base64.getDecoder().decode(base64PrivateKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }

    public AppleAuthTokenResponseDTO generateAuthToken(String token)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            return appleAuthClient.getAccessToken(token, clientId, createClientSecret(),GRANT_TYPE_AUTH_CODE);
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            throw new IllegalArgumentException("Apple Auth Access Token 에러 발생");
        }
    }

}
