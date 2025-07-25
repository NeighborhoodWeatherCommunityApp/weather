package org.pknu.weather.member.auth.controller;

import io.jsonwebtoken.ExpiredJwtException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.auth.service.AuthService;
import org.pknu.weather.security.exception.TokenException;
import org.pknu.weather.security.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;

    @PostMapping("/token")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String tokenType = request.get("type");
        String accessToken = request.get("accessToken");

        if(tokenType == null || tokenType.isBlank()) {
            log.error("social 로그인의 타입에 문제가 발생했습니다.");
            throw new GeneralException(ErrorStatus.TYPE_NOT_ACCEPTED);
        }

        Map<String, String> appTokens = authService.generateTokens(tokenType, accessToken);

        return ResponseEntity.ok(appTokens);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> tokens) {

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        checkAccessToken(accessToken);

        Map<String, String> appTokens = authService.refreshTokens(refreshToken);

        return ResponseEntity.ok(appTokens);
    }

    private void checkAccessToken(String accessToken) throws TokenException {
        try{
            jwtUtil.validateToken(accessToken);
        }catch (ExpiredJwtException expiredJwtException){
            log.info("Access Token has expired");
        }catch(Exception exception){
            throw new TokenException(ErrorStatus.MALFORMED_ACCESS_TOKEN);
        }
    }

}
