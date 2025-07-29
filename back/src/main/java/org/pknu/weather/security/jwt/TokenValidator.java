package org.pknu.weather.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.security.exception.TokenException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final JWTUtil jwtUtil;

    public Map<String, Object> validateAccessToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (MalformedJwtException e) {
            throw new TokenException(ErrorStatus.MALFORMED_ACCESS_TOKEN);
        } catch (SignatureException e) {
            throw new TokenException(ErrorStatus.BAD_SIGNED_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new TokenException(ErrorStatus.EXPIRED_ACCESS_TOKEN);
        }
    }

    public Map<String, Object> validateRefreshToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (MalformedJwtException e) {
            throw new TokenException(ErrorStatus.MALFORMED_REFRESH_TOKEN);
        } catch (SignatureException e) {
            throw new TokenException(ErrorStatus.BAD_SIGNED_REFRESH_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new TokenException(ErrorStatus.EXPIRED_REFRESH_TOKEN);
        }
    }
}
