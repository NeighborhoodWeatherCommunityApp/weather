package org.pknu.weather.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.pknu.weather.aop.util.LoggingUtils;
import org.pknu.weather.common.converter.TokenConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Order(1)
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(value = "log-option", havingValue = "prod", matchIfMissing = true)
public class ProdLoggingAspect {

    /**
     * Controller, Service 에 모든 메서드 수행을 로깅하는 AOP입니다. 쓰레드 id, 클래스명, 메서드명, 파라미터, 메서드 수행 시간 등이 로그에 남습니다.
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("org.pknu.weather.aop.Pointcuts.prodLoggingPointcut()")
    public Object doLog(ProceedingJoinPoint pjp) throws Throwable {
        try {
            StringBuilder sb = new StringBuilder();
            Object[] args = pjp.getArgs();
            Signature signature = pjp.getSignature();
            String fullName = signature.getDeclaringTypeName();
            String traceId = String.valueOf(Thread.currentThread().getId());
            String className = fullName.substring(fullName.lastIndexOf(".") + 1);
            String methodName = signature.getName();

            List<Object> argList = Arrays.stream(args)
                    .map(obj -> {
                        if (obj instanceof String str && str.startsWith("Bearer ")) {
                            return TokenConverter.getEmailByToken(str);
                        }
                        return obj;
                    })
                    .toList();

            sb.append(String.format("\n[%s] %s.%s args=(%s)",
                    traceId, className, methodName,
                    argList
            ));

            log.info(sb.toString());
            Object result = pjp.proceed();
            return result;
        } catch (Exception e) {
            LoggingUtils.logError(pjp, e);
            throw e;
        }
    }
}