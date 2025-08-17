package org.pknu.weather.aop;

import java.net.InetAddress;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.pknu.weather.apipayload.ApiResponse;
import org.pknu.weather.apipayload.code.status.SuccessStatus;
import org.pknu.weather.common.GlobalParams;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(0)
@Aspect
@Component
@RequiredArgsConstructor
public class LocationCheckAspect {
    private final MemberRepository memberRepository;

    /**
     * 사용자가 지역을 등록했는지 확인하는 공통 로직
     *
     * @param authorization JWT 입니다.
     * @return
     * @throws Throwable
     */
    @Around("org.pknu.weather.aop.Pointcuts.doCheckLocationPointcut() && args(authorization,..)")
    public Object locationCheck(ProceedingJoinPoint pjp, String authorization) throws Throwable {
        String email = TokenConverter.getEmailByToken(authorization);
        Member member = memberRepository.safeFindByEmail(email);

        if (member.getLocation() == null) {
            String address = "http://" + InetAddress.getLocalHost().getHostAddress();
            Map<String, String> result = Map.of("url", address + GlobalParams.LOCATION_REDIRECT_URL);
            return ApiResponse.of(SuccessStatus._REDIRECT, SuccessStatus._REDIRECT.getMessage(), result);
        }

        return pjp.proceed();
    }
}
