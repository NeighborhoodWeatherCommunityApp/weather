package org.pknu.weather.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.apiPayload.code.status.SuccessStatus;
import org.pknu.weather.common.GlobalParams;
import org.pknu.weather.service.MemberQueryService;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Map;


@Aspect
@Component
@RequiredArgsConstructor
public class LocationCheckAspect {
    private final MemberQueryService memberQueryService;

    /**
     * 사용자가 지역을 등록했는지 확인하는 공통 로직
     *
     * @param pjp
     * @param memberId TODO: email로 대체될 예정
     * @return
     * @throws Throwable
     */
    @Around("org.pknu.weather.aop.Pointcuts.doCheckLocationPointcut() && args(memberId)")
    public Object locationCheck(ProceedingJoinPoint pjp, Long memberId) throws Throwable {
        if (!memberQueryService.hasRegisteredLocation(memberId)) {
            String address = "http://" + InetAddress.getLocalHost().getHostAddress();
            Map<String, String> result = Map.of("url", address + GlobalParams.LOCATION_REDIRECT_URL);
            return ApiResponse.of(SuccessStatus._REDIRECT, SuccessStatus._REDIRECT.getMessage(), result);
        }

        return pjp.proceed();
    }
}
