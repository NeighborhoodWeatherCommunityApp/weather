package org.pknu.weather.member.auth.userInfo;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.member.auth.feignClient.KaKaoAuthClient;
import org.pknu.weather.member.auth.dto.KakaoUserResponseDTO;
import org.pknu.weather.security.exception.TokenException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoUserInfoProvider implements UserInfoProvider {

    private final KaKaoAuthClient kaKaoAuthClient;

    @Override
    public SocialUserInfo getUserInfo(String accessToken) throws TokenException{
        log.debug("KakaoUserInfoProvider start.................");

        String authHeader = "Bearer " + accessToken;

        checkToken(authHeader);

        KakaoUserResponseDTO kakaoUser = kaKaoAuthClient.getMemberData(authHeader, "application/x-www-form-urlencoded;charset=utf-8");

        return new KakaoUserInfo("kakao", kakaoUser.getKakao_account().getEmail(), kakaoUser.getId());
    }

    private void checkToken(String authHeader) throws TokenException{
        Response result = kaKaoAuthClient.checkKakaoAccessToken(authHeader);
        int status = result.status();
        switch (status){
            case 200:
                break;
            case 1:
                throw new TokenException(ErrorStatus.KAKAO_SERVER_ERROR);
            case 2:
                throw new TokenException(ErrorStatus.KAKAO_ACCESS_TOKEN_BAT_TYPE);
            case 401:
                throw new TokenException(ErrorStatus.MALFORMED_KAKAO_ACCESS_TOKEN);
            default:
                throw new RuntimeException();
        }
    }

}
