package org.pknu.weather.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(2)
@Slf4j
@Component
public class ExecuteTimeAspect {

    @Around("org.pknu.weather.aop.Pointcuts.transactionalPointcut() && args(memberId)")
    public Object recordExecuteTIme(ProceedingJoinPoint pjp, Long memberId) throws Throwable {
        long current = System.currentTimeMillis();

        Object result = pjp.proceed();

        long end = System.currentTimeMillis();
        long time = end - current;

        log.info("time = {}ms", time);

        return result;
    }
}
