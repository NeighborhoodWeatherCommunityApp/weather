package org.pknu.weather.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.pknu.weather.aop.util.ExecutionTimerUtils;
import org.pknu.weather.aop.util.LoggingUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(value = "integration-logging", havingValue = "true", matchIfMissing = true)
public class IntegrationLoggingAspect {

    /**
     * Controller, Service 에 모든 메서드 수행을 로깅하는 AOP입니다. 쓰레드 id, 클래스명, 메서드명, 파라미터, 메서드 수행 시간 등이 로그에 남습니다.
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("org.pknu.weather.aop.Pointcuts.integrationLoggingPointcut()")
    public Object doLog(ProceedingJoinPoint pjp) throws Throwable {
        try {
            LoggingUtils.logBefore(pjp, pjp.getArgs());
            long start = ExecutionTimerUtils.start();

            Object result = pjp.proceed();

            long end = ExecutionTimerUtils.end(start);
            LoggingUtils.logAfterWithExecutionTime(pjp, end);
            return result;
        } catch (Exception e) {
            LoggingUtils.logError(pjp, e);
            throw e;
        }
    }
}