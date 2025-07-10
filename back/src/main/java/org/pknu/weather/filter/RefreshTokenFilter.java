package org.pknu.weather.filter;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.security.exception.TokenException;
import org.pknu.weather.security.util.JWTUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Refresh Token Filter run.......................");

        try {
            Map<String, String> tokens = parseRequestJSON(request);

            String accessToken = tokens.get("accessToken");
            String refreshToken = tokens.get("refreshToken");

            checkAccessToken(accessToken);

            Map<String, Object> refreshClaims = checkRefreshToken(refreshToken);
            Long exp = ((Number) refreshClaims.get("exp")).longValue();
            getNewClaims(refreshClaims);

            String renewedAccessToken = jwtUtil.generateToken(refreshClaims, 3);
            String renewedRefreshToken = tokens.get("refreshToken");

            if (isRefreshTokenRenewNeed(exp)){
                log.info("new Refresh Token required.................");
                renewedRefreshToken = jwtUtil.generateToken(refreshClaims, 30);
            }

            sendTokens(renewedAccessToken, renewedRefreshToken, response);


        } catch (TokenException tokenException) {
            tokenException.sendResponseError(response);
        } catch (Exception e) {
            log.error("Json 파싱 실패 {}", e.getMessage());
        }

    }

    private static void getNewClaims(Map<String, Object> refreshClaims) {
        refreshClaims.remove("iat");
        refreshClaims.remove("exp");
    }

    private boolean isRefreshTokenRenewNeed(Long exp) {

        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);

        Date current = new Date(System.currentTimeMillis());

        return ((expTime.getTime() - current.getTime()) < (1000 * 60 * 60 * 24 * 3 ));
    }



    private Map<String,String> parseRequestJSON(HttpServletRequest request) throws TokenException {

        Map<String,String> tokens = null;
        try(Reader reader = new InputStreamReader(request.getInputStream())){
            Gson gson = new Gson();
            tokens = gson.fromJson(reader, Map.class);

        } catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException();
        }

        if(tokens == null)
            throw new TokenException(ErrorStatus.TOKENS_NOT_ACCEPTED);

        return tokens;
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

    private Map<String, Object> checkRefreshToken(String refreshToken) throws TokenException{

        try {
            return jwtUtil.validateToken(refreshToken);
        }catch(ExpiredJwtException expiredJwtException){
            throw new TokenException(ErrorStatus.EXPIRED_REFRESH_TOKEN);
        }catch(Exception exception){
            throw new TokenException(ErrorStatus.MALFORMED_REFRESH_TOKEN);
        }
    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        Gson gson = new Gson();

        Map<String, String> tokens = Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue);

        String responseStr = gson.toJson(ApiResponse.onSuccess(tokens));

        try {
            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}