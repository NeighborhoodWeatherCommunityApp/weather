package org.pknu.weather.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Slf4j
@Aspect
@Component
public class BusinessLogicLoggingAspect {

    // @Transactional 이 붙어있으면서 매개변수 memberId 를 포함하는 모든 메서드들에 대해서 로깅
    @Around("org.pknu.weather.aop.Pointcuts.transactionalPointcut() && args(memberId,..)")
    public Object doLog(ProceedingJoinPoint pjp, Long memberId) throws Throwable {
        Signature signature = pjp.getSignature();

        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
//        Object[] args = pjp.getArgs();

        try {
            log.info("[memberId = {}] [****\t트랜잭션 시작\t\t****] {}, {}", memberId, className, methodName);
            Object result = pjp.proceed();
            log.info("[memberId = {}] [****\t트랜잭션 커밋\t\t****] {}, {}", memberId, className, methodName);
            return result;
        } catch (Exception e) {
            log.info("[memberId = {}] [****\t트랜잭션 롤백\t\t****] {}, {},", memberId, className, methodName);
            throw e;
        }
    }
}