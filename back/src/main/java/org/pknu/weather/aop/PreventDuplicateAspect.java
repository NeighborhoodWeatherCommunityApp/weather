package org.pknu.weather.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.exception.GeneralException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(-1)
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PreventDuplicateAspect {
    private final CacheManager cm;
    private static final String CACHE_NAME = "duplicateRequestCache";

    @Around(value = "org.pknu.weather.aop.Pointcuts.mainPageControllerV1Pointcut() && args(authorization,..)"
            , argNames = "joinPoint,authorization")
    public Object preventDuplicate(ProceedingJoinPoint joinPoint, String authorization)
            throws Throwable {
        String email = TokenConverter.getEmailByToken(authorization);
        String key = buildKey(email, joinPoint);
        Cache cache = cm.getCache(CACHE_NAME);

        if (cache != null && cache.get(key) != null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST_DUPLICATED);
        }

        cache.put(key, true);

        try {
            return joinPoint.proceed();
        } finally {
            cache.evict(key);
        }
    }

    private String buildKey(String email, ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        return methodName + ":" + email;
    }
}
