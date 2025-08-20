package org.pknu.weather.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
@Slf4j
public class RedisExceptionLoggingAspect {

    @Around("org.pknu.weather.aop.Pointcuts.redisPointcut()")
    public Object catchRedisException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            log.error("[Redis 예외] {} - args: {}, message: {}",
                    joinPoint.getSignature().toShortString(),
                    Arrays.toString(joinPoint.getArgs()),
                    ex.getMessage(), ex);

            // 실패 시 fallback 처리
            Class<?> returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
            if (returnType.equals(List.class)) return Collections.emptyList();
            if (returnType.equals(Boolean.class) || returnType.equals(boolean.class)) return false;
            if (returnType.equals(Void.TYPE)) return null;
            return null; // 기본 fallback
        }
    }
}
