package org.pknu.weather.common.utils;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.feignClient.KaKaoLoginClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoLoginUtils {

    private final KaKaoLoginClient kaKaoLoginClient;
    private static final String targetIdType = "user_id";

    @Value("${spring.kakao.admin_key}")
    private String adminKey;

    public void unlinkMember(Long targetId) {
        String adminKeyFormat = "KakaoAK " + adminKey;
        Response result = kaKaoLoginClient.deleteMemberData(adminKeyFormat, targetIdType, targetId);

        validateResponse(result);
    }

    private static void validateResponse(Response result) {
        if (result.status() == 400) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
        if (result.status() == 401) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }
        if (result.status() >= 500) {
            throw new GeneralException(ErrorStatus._API_SERVER_ERROR);
        }
    }
}
